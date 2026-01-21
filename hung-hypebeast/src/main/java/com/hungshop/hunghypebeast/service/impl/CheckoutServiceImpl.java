package com.hungshop.hunghypebeast.service.impl;

import com.hungshop.hunghypebeast.dto.CartContext;
import com.hungshop.hunghypebeast.dto.response.OrderDetailDto;
import com.hungshop.hunghypebeast.dto.request.CheckoutRequest;
import com.hungshop.hunghypebeast.service.EmailService;
import com.hungshop.hunghypebeast.entity.Cart;
import com.hungshop.hunghypebeast.entity.CartItem;
import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.entity.OrderItem;
import com.hungshop.hunghypebeast.entity.Payment;
import com.hungshop.hunghypebeast.entity.ProductInventory;
import com.hungshop.hunghypebeast.entity.TrackingToken;
import com.hungshop.hunghypebeast.entity.Order.OrderStatus;
import com.hungshop.hunghypebeast.entity.Payment.PaymentMethod;
import com.hungshop.hunghypebeast.entity.Payment.PaymentStatus;
import com.hungshop.hunghypebeast.exception.OutOfStockException;
import com.hungshop.hunghypebeast.exception.ResourceNotFoundException;
import com.hungshop.hunghypebeast.repository.*;
import com.hungshop.hunghypebeast.service.CheckoutService;
import com.hungshop.hunghypebeast.service.SePayClient;
import com.hungshop.hunghypebeast.mapping.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final TrackingTokenRepository trackingTokenRepository;
    private final EmailService emailService;
    private final SePayClient sePayClient;

    public CheckoutServiceImpl(CartRepository cartRepository,
                               ProductInventoryRepository productInventoryRepository,
                               OrderRepository orderRepository,
                               PaymentRepository paymentRepository,
                               TrackingTokenRepository trackingTokenRepository,
                               EmailService emailService,
                               SePayClient sePayClient) {
        this.cartRepository = cartRepository;
        this.productInventoryRepository = productInventoryRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.trackingTokenRepository = trackingTokenRepository;
        this.emailService = emailService;
        this.sePayClient = sePayClient;
    }

    @Override
    public OrderDetailDto checkout(CartContext context, CheckoutRequest request) {
        Cart cart = findCartForCheckout(context);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // Lock and validate inventory per item
        long totalAmount = 0L;
        List<CartItem> items = cart.getItems();
        List<ProductInventory> lockedInventories = new ArrayList<>();

        for (CartItem item : items) {
            Long variantId = item.getVariant().getId();
            ProductInventory inventory = productInventoryRepository.findByVariantIdWithLock(variantId)
                    .orElseThrow(() -> new OutOfStockException("Inventory not found for variant: " + variantId));

            long requested = item.getQuantity();
            if (inventory.getAvailableQuantity() == null || inventory.getAvailableQuantity() < requested) {
                throw new OutOfStockException("Not enough stock for variant: " + item.getVariant().getSku());
            }

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - requested);
            inventory.setSoldQuantity(inventory.getSoldQuantity() + requested);
            lockedInventories.add(inventory);

            long price = item.getVariant().getPrice();
            totalAmount += price * requested;
        }

        // Persist inventory updates
        productInventoryRepository.saveAll(lockedInventories);

        // Create order and items
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setShippingAddress(request.getCustomerAddress());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtTime(cartItem.getVariant().getPrice());
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        order = orderRepository.save(order);

        // Create payment (COD hoặc SEPAY)
        Payment payment = new Payment();
        payment.setOrder(order);
        PaymentMethod method = resolvePaymentMethod(request.getPaymentMethod());
        payment.setMethod(method);

        if (method == PaymentMethod.SEPAY) {
            // Tích hợp SePay: tạo QR thanh toán và lưu lại reference
            SePayClient.SePayPaymentResponse sePayResponse = sePayClient.createQrPayment(order, totalAmount);
            payment.setStatus(PaymentStatus.PENDING_QR);
            payment.setSePayQrCode(sePayResponse.getQrCodeUrl());
            payment.setSePayReference(sePayResponse.getReference());
        } else {
            // Mặc định: COD
            payment.setStatus(PaymentStatus.PENDING);
        }
        payment = paymentRepository.save(payment);

        // Create tracking token
        TrackingToken trackingToken = new TrackingToken();
        trackingToken.setOrder(order);
        trackingToken.setToken(UUID.randomUUID().toString());
        trackingToken.setExpiresAt(LocalDateTime.now().plusDays(30));
        trackingTokenRepository.save(trackingToken);

        // Gửi email xác nhận + link tracking (stub)
        emailService.sendOrderConfirmation(order);
        emailService.sendOrderTracking(order, trackingToken.getToken());

        // Clear cart after successful order
        cart.getItems().clear();
        cartRepository.save(cart);

        return OrderMapper.toDetailDto(order, payment, trackingToken.getToken());
    }

    private Cart findCartForCheckout(CartContext context) {
        if (context == null || context.getSessionId() == null || context.getSessionId().isBlank()) {
            throw new IllegalArgumentException("SessionId must be provided for checkout");
        }

        Optional<Cart> cartOpt = cartRepository.findBySessionId(context.getSessionId());

        return cartOpt.orElseThrow(() -> new ResourceNotFoundException("Cart not found for checkout"));
    }

    private String generateOrderNumber() {
        // Đơn giản: dùng UUID rút gọn, sau này có thể đổi sang format HH-YYYYMMDD-XXXX
        return "HH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentMethod resolvePaymentMethod(String method) {
        if (method == null) {
            return PaymentMethod.COD;
        }
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PaymentMethod.COD;
        }
    }

}
