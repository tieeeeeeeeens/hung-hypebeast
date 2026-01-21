# Database Design - HUNG HYPEBEAST E-Commerce


## Chi tiết ERD

- File hình: ![Database ERD](<HUNG HYPEBEAST E-Commerce_ERD.png>)
- File code-gen (PlantUML): `HUNG_HYPEBEAST_ERD.puml`

File `.puml` được sinh ra trực tiếp theo schema hiện tại của các entity JPA (category, product, product_variant, product_image, product_inventory, cart, cart_item, reservation, orders, order_item, payment, tracking_token, email_log). Khi cần cập nhật ERD, chỉ cần chỉnh lại entity hoặc file `.puml` này, sau đó render lại hình.

## 📋 Giải thích Thiết kế

### **1. category**
**Tại sao cần?** 
- Phân loại sản phẩm (Áo thun, Hoodie, Quần, v.v.)
- Cho phép lọc theo Category

---

### **2. product**
**Tại sao cần?**
- Lưu thông tin chung sản phẩm (Áo Thun Rồng)
- Tách riêng khỏi variant vì 1 sản phẩm có nhiều biến thể

**Lý do tách bảng:**
- Một sản phẩm có nhiều variant (Size, Color)
- Tránh duplicate data (name, description)

---

### **3. product_variant**
**Tại sao cần?**
- Lưu từng biến thể cụ thể (Size L Đen, Size M Trắng)
- Mỗi variant = 1 SKU
- Giá có thể khác nhau giữa các variant

**Ví dụ:**
```
Product: Áo Thun Rồng
├─ Variant 1: Size L, Đen (SKU: AOTUNRONG-L-DEN) - 150k
├─ Variant 2: Size L, Trắng (SKU: AOTUNRONG-L-TRANG) - 150k
├─ Variant 3: Size XL, Đen (SKU: AOTUNRONG-XL-DEN) - 160k
```

---

### **4. product_image**
**Tại sao cần?**
- Lưu danh sách ảnh cho mỗi sản phẩm (thumbnail, gallery)
- Hỗ trợ nhiều ảnh / 1 sản phẩm, và có thể gắn riêng cho từng variant nếu cần

**Thiết kế:**
- `product_id`: bắt buộc, ảnh thuộc về product
- `variant_id`: nullable, nếu set thì ảnh dùng riêng cho 1 variant (ví dụ hình zoom màu đỏ)
- `url`: đường dẫn ảnh (CDN / S3 / static)
- `sort_order`: số thứ tự để backend sắp xếp trước khi trả về

**Ý nghĩa:**
- Frontend dễ render gallery đẹp (thumbnail + chi tiết)
- Có thể mở rộng sau này cho ảnh 360°, video, v.v.

---

### **5. product_inventory**
**Tại sao cần?**
- Tracking tồn kho chi tiết cho mỗi variant
- Chia tách: `available + reserved + sold = total`
- Ngăn oversell

**Lý do chia bảng:**
- Tồn kho là dynamic, cần update thường xuyên
- Tách riêng để query nhanh (có index)

---

### **6. cart & cart_item**
**Tại sao cần 2 bảng?**
- `cart`: Giỏ hàng theo session (1 session = 1 giỏ đang active)
- `cart_item`: Mục trong giỏ (1 giỏ = nhiều mục)

**Session ID?**
*Hiện tại hệ thống chỉ hỗ trợ guest (không có login).* Toàn bộ giỏ hàng được gắn với `session_id`:
- Khách anonymous: mỗi browser / thiết bị có một `session_id`
- Bảng `cart` chỉ lưu `session_id` (không còn `user_id`)

Khi checkout xong, cart không còn được dùng lại cho đơn đó, nhưng vẫn có thể tạo cart mới cho cùng session nếu tiếp tục mua hàng.

---

### **7. reservation**
**Tại sao cần?**
- Tracking "giữ chỗ" khi khách add to cart / checkout
- Kiểm soát deadline (mặc định 10 phút, cấu hình qua `app.reservation.ttl-minutes`)
- CRON job quét để auto-release

**Status:**
- `RESERVED`: Đang giữ (trong 10 phút)
- `CONFIRMED`: Thanh toán xong
- `CANCELLED`: Khách hủy
- `EXPIRED`: Quá hạn (auto)

---

### **8. order & order_item**
**Tại sao 2 bảng?**
- `order`: Thông tin đơn chính
- `order_item`: Chi tiết các mục trong đơn

**Order Status Flow:**
```
PENDING → CONFIRMED → PAID → SHIPPING → DELIVERED
                   ↘ CANCELLED
```

---

### **9. payment**
**Tại sao cần?**
- Lưu thông tin thanh toán riêng
- Track payment status (PENDING, COMPLETED, FAILED)
- SePay QR code, reference

**SePay Integration:**
- `sepay_qr_code`: URL hình QR
- `sepay_reference`: Mã từ SePay callback

---

### **10. tracking_token**
**Tại sao cần?**
- Guest tracking qua email link mà không login
- Token độc lập = không cần account
- Expire sau 90 ngày

**Ví dụ link:**
```
https://hypebeast.vn/orders/track?token=a1b2c3d4-e5f6-4g7h-i8j9-k0l1m2n3o4p5
```

---

### **11. email_log**
**Tại sao cần?**
- Track email nào đã send, cái nào failed
- Debug, resend email nếu cần

---

## 🔗 Giải thích Quan hệ (Relationships)

### **1. category → product (One-to-Many)**
```
1 Category có nhiều Product
Ví dụ:
  Category: "Áo Thun"
    ├─ Product: "Áo Thun Rồng"
    ├─ Product: "Áo Thun Sư Tử"
    └─ Product: "Áo Thun Mèo"

Foreign Key: product.category_id → category.id
```

**Ý nghĩa:**
- Lọc sản phẩm theo loại dễ hơn
- Nếu xóa category → xóa tất cả product (CASCADE)

---

### **2. product → product_variant (One-to-Many)**
```
1 Product có nhiều Variant
Ví dụ:
  Product: "Áo Thun Rồng"
    ├─ Variant: Size L, Đen (SKU: AOTUNRONG-L-DEN)
    ├─ Variant: Size L, Trắng (SKU: AOTUNRONG-L-TRANG)
    ├─ Variant: Size XL, Đen (SKU: AOTUNRONG-XL-DEN)
    └─ Variant: Size XL, Trắng (SKU: AOTUNRONG-XL-TRANG)

Foreign Key: product_variant.product_id → product.id
```

**Ý nghĩa:**
- Mỗi variant có SKU riêng, giá có thể khác nhau
- Inventory track riêng từng variant

---

### **3. product_variant → product_inventory (One-to-One)**
```
1 Variant = 1 InventoryRecord
Ví dụ:
  Variant: "AOTUNRONG-L-DEN"
    └─ Inventory: total=100, available=50, reserved=30, sold=20

Foreign Key: product_inventory.variant_id → product_variant.id (UNIQUE)
```

**Ý nghĩa:**
- Mỗi variant có 1 record inventory
- Dễ query nhanh tồn kho
- Bất biến: available + reserved + sold = total

---

### **4. cart (session-based) → cart_item (One-to-Many)**
```
1 Cart (gắn với 1 session) có nhiều CartItem
Ví dụ:
  Cart: [cart_id=1, session_id=abc123]
    ├─ CartItem: Variant-1 x 2 cái
    ├─ CartItem: Variant-2 x 1 cái
    └─ CartItem: Variant-3 x 5 cái

Foreign Key: cart_item.cart_id → cart.id
```

**Ý nghĩa:**
- Không còn khái niệm user/account trong DB
- Mỗi session đang mua hàng tương ứng 1 cart active
- Dễ xoá/clear giỏ khi khách đổi ý hoặc khi checkout xong

### **5. product_variant → cart_item (One-to-Many)**
```
1 Variant có thể ở nhiều giỏ hàng
Ví dụ:
  Variant: "AOTUNRONG-L-DEN"
    ├─ CartItem: (cart=1, qty=2)
    ├─ CartItem: (cart=2, qty=1)
    └─ CartItem: (cart=3, qty=5)

Foreign Key: cart_item.variant_id → product_variant.id
```

**Ý nghĩa:**
- Cùng 1 variant có thể ở nhiều giỏ hàng của nhiều khách
- Query "Ai đang mua cái này?" được dễ dàng

### **6. product_variant → reservation (One-to-Many)**
```
1 Variant có thể bị nhiều khách giữ
Ví dụ:
  Variant: "AOTUNRONG-L-DEN"
    ├─ Reservation: (customer=A, RESERVED, expires 10:10)
    ├─ Reservation: (customer=B, CONFIRMED, expires 10:15)
    └─ Reservation: (customer=C, EXPIRED, expires 10:05)

Foreign Key: reservation.variant_id → product_variant.id
```

**Ý nghĩa:**
- Tracking "ai đang giữ cái áo này"
- Ngăn oversell
- Query: "Còn bao nhiêu cái có sẵn?" = available - (count RESERVED)

### **7. order → order_item (One-to-Many)**
```
1 Order có nhiều OrderItem
Ví dụ:
  Order: [HH-20260109-001]
    ├─ OrderItem: (Variant-1, qty=2, price=300k)
    ├─ OrderItem: (Variant-2, qty=1, price=150k)
    └─ OrderItem: (Variant-3, qty=3, price=450k)
    
    Total: 900k

Foreign Key: order_item.order_id → order.id
```

**Ý nghĩa:**
- 1 đơn hàng có nhiều mục
- Lưu giá lúc đặt hàng (có thể khác giá hiện tại)
- Tính total = sum(qty * price)

### **8. product_variant → order_item (One-to-Many)**
```
1 Variant có thể xuất hiện trong nhiều Order
Ví dụ:
  Variant: "AOTUNRONG-L-DEN"
    ├─ OrderItem: (order=HH-001, qty=2)
    ├─ OrderItem: (order=HH-002, qty=1)
    └─ OrderItem: (order=HH-003, qty=3)
    
    Total: 6 cái đã bán

Foreign Key: order_item.variant_id → product_variant.id
```

**Ý nghĩa:**
- Report: "Cái áo này bán được bao nhiêu?"
- Top seller analysis
- Stock forecast

### **9. order → payment (One-to-One)**
```
1 Order = 1 Payment record
Ví dụ:
  Order: [HH-20260109-001, total=900k]
    └─ Payment: [COD, status=PENDING]
  
  Order: [HH-20260109-002, total=300k]
    └─ Payment: [SEPAY, status=COMPLETED, qr_code=..., ref=ABC123]

Foreign Key: payment.order_id → order.id (UNIQUE)
```

**Ý nghĩa:**
- 1 đơn hàng = 1 cách thanh toán
- Track payment method (COD, SEPAY)
- Track payment status (PENDING → COMPLETED)

---

### **13. order → tracking_token (One-to-One)**
```
1 Order = 1 TrackingToken
Ví dụ:
  Order: [HH-20260109-001]
    └─ TrackingToken: [token=a1b2c3d4-..., expires=90 days]

Khách track qua link:
  https://hypebeast.vn/orders/track?token=a1b2c3d4-...

Foreign Key: tracking_token.order_id → order.id
```

**Ý nghĩa:**
- Guest tracking mà không login
- Token unique, expire sau 90 ngày
- Bảo vệ: không ai có token thì không thấy info

---

### **14. order → email_log (One-to-Many)**
```
1 Order có nhiều email logs
Ví dụ:
  Order: [HH-20260109-001]
    ├─ EmailLog: [CONFIRMATION, sent=10:10, status=SENT]
    ├─ EmailLog: [TRACKING, sent=10:10, status=SENT]
    └─ EmailLog: [REMINDER, sent=10:15, status=FAILED]

Foreign Key: email_log.order_id → order.id (NULL = email chung)
```

**Ý nghĩa:**
- Track "email nào đã gửi rồi"
- Debug: "Tại sao khách không nhận email?"
- Resend: "Gửi lại email thất bại"

---

### **15. product → product_image (One-to-Many)**
```
1 Product có nhiều ProductImage
Ví dụ:
  Product: "Áo Thun Rồng"
    ├─ ProductImage: (url=..., sort_order=1)
    ├─ ProductImage: (url=..., sort_order=2)
    └─ ProductImage: (url=..., sort_order=3)

Foreign Key: product_image.product_id → product.id
```

**Ý nghĩa:**
- Backend trả về danh sách ảnh theo sort_order để FE hiển thị gallery
- Dễ mở rộng thêm ảnh mới mà không phải sửa bảng product

---

### **16. product_variant → product_image (One-to-Many, optional)**
```
1 Variant có thể có ảnh riêng (không bắt buộc)
Ví dụ:
  Variant: "AOTUNRONG-L-RED"
    └─ ProductImage: (url=ảnh riêng màu đỏ, sort_order=1)

Foreign Key: product_image.variant_id → product_variant.id (NULL = ảnh dùng chung cho product)
```

**Ý nghĩa:**
- Hỗ trợ ảnh riêng cho từng màu / size nếu cần
- Nếu variant_id NULL → ảnh dùng chung cho toàn bộ product

---

## 📐 Data Flow (Luồng dữ liệu)

### **Khi Khách Browse (Duyệt sản phẩm):**
```
1. Query: GET /api/products?category=1&price_min=100&price_max=500
2. Database: category → product → product_variant → product_inventory
3. Return: Danh sách variant với available_quantity
```

### **Khi Khách Add to Cart:**
```
// Nếu login:
1. Get: user từ token (user_id = 123)
2. Create/Get: cart (user_id=123)

// Nếu guest:
1. Dùng session_id để identify cart
2. Create/Get: cart (user_id=NULL, session_id=abc123)

3. Create/Update: cart_item (variant + qty + price)
4. Create/Update: reservation (status=RESERVED, gắn với cart + variant, expires_at = NOW + TTL)
5. Update: product_inventory
  - available_quantity giảm tương ứng với số lượng được giữ
  - reserved_quantity tăng tương ứng
```

### **Khi Khách Thanh toán:**
```
// Nếu login (user_id=123):
1. Fetch: user info (phone, email, name, address)

// Nếu guest (user_id=NULL):
1. Request: phone, email, name, address từ khách

2. Verify: reservation chưa expire
3. Create: order (user_id=123 hoặc NULL)
4. Create: order_item (từ cart_item)
5. Create: payment (method + status)
6. Create: tracking_token (cho tracking)
7. Update: reservation (status=CONFIRMED)
8. Update: product_inventory
   - reserved_quantity -= qty
   - sold_quantity += qty
9. Send email + email_log
10. Delete: cart + cart_item (giỏ hàng xóa)
```

### **Khi CRON Job chạy (Auto-expire):**
```
1. Query: reservation chưa thanh toán + expires_at < NOW
2. For each:
   - Update: product_inventory (cộng lại available)
   - Update: reservation (status=EXPIRED)
```
