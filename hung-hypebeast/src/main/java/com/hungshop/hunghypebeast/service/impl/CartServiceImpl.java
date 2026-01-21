package com.hungshop.hunghypebeast.service.impl;

import com.hungshop.hunghypebeast.dto.request.AddCartItemRequest;
import com.hungshop.hunghypebeast.dto.CartContext;
import com.hungshop.hunghypebeast.dto.response.CartDto;
import com.hungshop.hunghypebeast.dto.request.UpdateCartItemRequest;
import com.hungshop.hunghypebeast.entity.Cart;
import com.hungshop.hunghypebeast.entity.CartItem;
import com.hungshop.hunghypebeast.entity.ProductInventory;
import com.hungshop.hunghypebeast.entity.ProductVariant;
import com.hungshop.hunghypebeast.entity.Reservation;
import com.hungshop.hunghypebeast.exception.OutOfStockException;
import com.hungshop.hunghypebeast.exception.ResourceNotFoundException;
import com.hungshop.hunghypebeast.repository.CartItemRepository;
import com.hungshop.hunghypebeast.repository.CartRepository;
import com.hungshop.hunghypebeast.repository.ProductInventoryRepository;
import com.hungshop.hunghypebeast.repository.ProductVariantRepository;
import com.hungshop.hunghypebeast.repository.ReservationRepository;
import com.hungshop.hunghypebeast.service.CartService;
import com.hungshop.hunghypebeast.mapping.CartMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final ReservationRepository reservationRepository;

    @Value("${app.reservation.ttl-minutes:10}")
    private long reservationTtlMinutes;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductVariantRepository productVariantRepository,
                           ProductInventoryRepository productInventoryRepository,
                           ReservationRepository reservationRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.productInventoryRepository = productInventoryRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public CartDto getCart(CartContext context) {
        Cart cart = findOrCreateCart(context);
        CartDto dto = CartMapper.toCartDto(cart);
        enrichStockStatus(cart, dto);
        return dto;
    }

    @Override
    public CartDto addItem(CartContext context, AddCartItemRequest request) {
        Cart cart = findOrCreateCart(context);

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + request.getVariantId()));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(i -> i.getVariant().getId().equals(variant.getId()))
                .findFirst();

        CartItem item;
        long oldQuantity = 0L;
        if (existingItemOpt.isPresent()) {
            item = existingItemOpt.get();
            oldQuantity = item.getQuantity() != null ? item.getQuantity() : 0L;
            long newQuantity = oldQuantity + request.getQuantity();
            item.setQuantity(newQuantity);
        } else {
            item = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.getQuantity().longValue())
                    .build();
            cart.getItems().add(item);
        }

        long newQuantity = item.getQuantity();
        long diff = newQuantity - oldQuantity;

        if (diff > 0) {
            ProductInventory inventory = productInventoryRepository.findByVariantIdWithLock(variant.getId())
                    .orElseThrow(() -> new OutOfStockException("Inventory not found for variant: " + variant.getId()));

            long available = inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : 0L;
            if (available < diff) {
                throw new OutOfStockException("Not enough stock for variant: " + variant.getSku());
            }

            inventory.setAvailableQuantity(available - diff);
            long reserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0L;
            inventory.setReservedQuantity(reserved + diff);
            productInventoryRepository.save(inventory);

            Reservation reservation = reservationRepository
                    .findByCartAndVariantAndStatus(cart, variant, Reservation.ReservationStatus.RESERVED)
                    .orElseGet(() -> {
                        Reservation r = new Reservation();
                        r.setCart(cart);
                        r.setVariant(variant);
                        r.setStatus(Reservation.ReservationStatus.RESERVED);
                        return r;
                    });

            long currentQty = reservation.getQuantity() != null ? reservation.getQuantity() : 0L;
            reservation.setQuantity(currentQty + diff);
            reservation.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(reservationTtlMinutes));
            reservationRepository.save(reservation);
        }

        cartRepository.save(cart);
        CartDto dto = CartMapper.toCartDto(cart);
        enrichStockStatus(cart, dto);
        return dto;
    }

    @Override
    public CartDto updateItem(CartContext context, Long cartItemId, UpdateCartItemRequest request) {
        Cart cart = findOrCreateCart(context);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + cartItemId));

        if (request.getQuantity() <= 0) {
            long releaseQty = item.getQuantity() != null ? item.getQuantity() : 0L;
            releaseInventoryAndReservation(cart, item.getVariant(), releaseQty);
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            long oldQuantity = item.getQuantity() != null ? item.getQuantity() : 0L;
            long newQuantity = request.getQuantity().longValue();
            long diff = newQuantity - oldQuantity;

            if (diff != 0) {
                ProductVariant variant = item.getVariant();
                ProductInventory inventory = productInventoryRepository.findByVariantIdWithLock(variant.getId())
                        .orElseThrow(() -> new OutOfStockException("Inventory not found for variant: " + variant.getId()));

                long available = inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : 0L;
                long reserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0L;

                if (diff > 0) {
                    if (available < diff) {
                        throw new OutOfStockException("Not enough stock for variant: " + variant.getSku());
                    }
                    inventory.setAvailableQuantity(available - diff);
                    inventory.setReservedQuantity(reserved + diff);
                } else {
                    long release = -diff;
                    inventory.setAvailableQuantity(available + release);
                    long newReserved = reserved - release;
                    if (newReserved < 0) {
                        newReserved = 0;
                    }
                    inventory.setReservedQuantity(newReserved);
                }

                productInventoryRepository.save(inventory);

                Reservation reservation = reservationRepository
                        .findByCartAndVariantAndStatus(cart, variant, Reservation.ReservationStatus.RESERVED)
                        .orElseGet(() -> {
                            Reservation r = new Reservation();
                            r.setCart(cart);
                            r.setVariant(variant);
                            r.setStatus(Reservation.ReservationStatus.RESERVED);
                            return r;
                        });

                long currentQty = reservation.getQuantity() != null ? reservation.getQuantity() : 0L;
                long newQty = currentQty + diff;
                if (newQty <= 0) {
                    reservation.setQuantity(0L);
                    reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
                } else {
                    reservation.setQuantity(newQty);
                    reservation.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(reservationTtlMinutes));
                }
                reservationRepository.save(reservation);
            }

            item.setQuantity(newQuantity);
        }

        cartRepository.save(cart);
        CartDto dto = CartMapper.toCartDto(cart);
        enrichStockStatus(cart, dto);
        return dto;
    }

    @Override
    public CartDto removeItem(CartContext context, Long cartItemId) {
        Cart cart = findOrCreateCart(context);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + cartItemId));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        CartDto dto = CartMapper.toCartDto(cart);
        enrichStockStatus(cart, dto);
        return dto;
    }

    @Override
    public void clearCart(CartContext context) {
        Cart cart = findOrCreateCart(context);
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                long qty = item.getQuantity() != null ? item.getQuantity() : 0L;
                releaseInventoryAndReservation(cart, item.getVariant(), qty);
            }
            cartItemRepository.deleteByCartId(cart.getId());
            cart.getItems().clear();
        }
    }

    private void releaseInventoryAndReservation(Cart cart, ProductVariant variant, long quantity) {
        if (quantity <= 0) {
            return;
        }

        ProductInventory inventory = productInventoryRepository.findByVariantIdWithLock(variant.getId())
                .orElse(null);
        if (inventory != null) {
            long available = inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity() : 0L;
            long reserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0L;

            inventory.setAvailableQuantity(available + quantity);
            long newReserved = reserved - quantity;
            if (newReserved < 0) {
                newReserved = 0;
            }
            inventory.setReservedQuantity(newReserved);
            productInventoryRepository.save(inventory);
        }

        reservationRepository.findByCartAndVariantAndStatus(cart, variant, Reservation.ReservationStatus.RESERVED)
                .ifPresent(reservation -> {
                    long currentQty = reservation.getQuantity() != null ? reservation.getQuantity() : 0L;
                    long newQty = currentQty - quantity;
                    if (newQty <= 0) {
                        reservation.setQuantity(0L);
                        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
                    } else {
                        reservation.setQuantity(newQty);
                        reservation.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(reservationTtlMinutes));
                    }
                    reservationRepository.save(reservation);
                });
    }

    private Cart findOrCreateCart(CartContext context) {
        String sessionId = context != null ? context.getSessionId() : null;

		if (sessionId == null || sessionId.isBlank()) {
			throw new IllegalArgumentException("SessionId must be provided");
		}

        Optional<Cart> cartOpt = cartRepository.findBySessionId(sessionId);

        if (cartOpt.isPresent()) {
            return cartOpt.get();
        }

        Cart cart = Cart.builder()
                .sessionId(sessionId)
                .build();
        cart.setItems(new java.util.ArrayList<>());

        return cartRepository.save(cart);
    }

    private void enrichStockStatus(Cart cart, CartDto cartDto) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return;
        }
        if (cartDto == null || cartDto.getItems() == null || cartDto.getItems().isEmpty()) {
            return;
        }

        Map<Long, CartItem> itemById = cart.getItems().stream()
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(CartItem::getId, Function.identity()));

        LocalDateTime now = LocalDateTime.now();

        cartDto.getItems().forEach(itemDto -> {
            Long itemId = itemDto.getId();
            CartItem entity = itemId != null ? itemById.get(itemId) : null;
            if (entity == null) {
                return;
            }

            ProductVariant variant = entity.getVariant();
            if (variant == null) {
                return;
            }

            long cartQty = entity.getQuantity() != null ? entity.getQuantity() : 0L;

            // Tính reservation còn hiệu lực cho chính cart này
            long reservedForCart = 0L;
            boolean hasValidReservation = false;
            Optional<Reservation> resOpt = reservationRepository
                    .findByCartAndVariantAndStatus(cart, variant, Reservation.ReservationStatus.RESERVED);
            if (resOpt.isPresent()) {
                Reservation res = resOpt.get();
                if (res.getExpiresAt() != null && res.getExpiresAt().isAfter(now)) {
                    Long q = res.getQuantity();
                    if (q != null && q > 0) {
                        reservedForCart = q;
                        hasValidReservation = true;
                    }
                }
            }

            ProductInventory inventory = productInventoryRepository.findByVariantId(variant.getId());
            long available = 0L;
            if (inventory != null && inventory.getAvailableQuantity() != null) {
                available = inventory.getAvailableQuantity();
            }

            long effectiveAvailable = available + (hasValidReservation ? reservedForCart : 0L);

            if (effectiveAvailable >= cartQty) {
                itemDto.setStockStatus("OK");
                itemDto.setMaxAvailable((int) Math.min(cartQty, Integer.MAX_VALUE));
            } else {
                itemDto.setStockStatus("NOT_ENOUGH");
                long max = Math.max(0L, Math.min(effectiveAvailable, Integer.MAX_VALUE));
                itemDto.setMaxAvailable((int) max);
            }
        });
    }

}

