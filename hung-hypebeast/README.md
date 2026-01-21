# HUNG HYPEBEAST Backend – Setup & API Overview

## 0. Setup Guide

### 0.1. Yêu cầu môi trường

- Java 17 (JDK 17)
- Maven 3.9+
- PostgreSQL 14+ (hoặc bản tương đương, chạy được server Postgres)

### 0.2. Cấu hình Database

1. Tạo database trống, ví dụ tên `hung_hypebeast`.
2. Cập nhật thông tin kết nối trong file `src/main/resources/application.yml`:
  - `spring.datasource.url=jdbc:postgresql://localhost:5432/hung_hypebeast`
  - `spring.datasource.username=...`
  - `spring.datasource.password=...`
3. Hibernate đang được cấu hình `spring.jpa.hibernate.ddl-auto=update` nên khi chạy app lần đầu, schema sẽ được tự động sinh.

### 0.3. Migration / Seed dữ liệu mẫu

- **Migration (tạo bảng)**
  1. Đảm bảo đã tạo database rỗng tên `hung_hypebeast`.
  2. Cập nhật đúng `spring.datasource.username` và `spring.datasource.password` trong `src/main/resources/application.yml`.
  3. Trong IntelliJ, run class `HungHypebeastApplication` (hoặc chạy `mvn spring-boot:run`).
     - Lần chạy đầu tiên, Hibernate sẽ tự động tạo toàn bộ bảng trong DB (do `spring.jpa.hibernate.ddl-auto=update`).

- **Seed dữ liệu mẫu**
  4. Trong IntelliJ, mở tab **Database** và kết nối tới DB `hung_hypebeast` (nếu chưa kết nối).
  5. Chuột phải vào database `hung_hypebeast` → **New | Console** (hoặc tương đương) để mở SQL console.
  6. Mở file `db/seed/demo_data.sql` trong IntelliJ, copy toàn bộ nội dung, dán vào SQL console và bấm Run.


### 0.4. Cách chạy server

**Cách 1 : chạy bằng IntelliJ**

1. Mở class `HungHypebeastApplication` tại `src/main/java/com/hungshop/hunghypebeast/HungHypebeastApplication.java`.
2. Ở bên trái số dòng, bấm icon tam giác xanh (**Run**) → chọn `Run 'HungHypebeastApplication'`.
3. Trong tab **Run**, khi thấy log `Started HungHypebeastApplication` và `Tomcat started on port(s): 8080` là server đã chạy.

**Cách 2: dùng Maven command line**

Trong thư mục `hung-hypebeast` (chứa file `pom.xml`):

```bash
mvn spring-boot:run
```

Hoặc build jar rồi chạy:

```bash
mvn clean package
java -jar target/hung-hypebeast-0.0.1-SNAPSHOT.jar
```

Server mặc định chạy ở `http://localhost:8080`.

### 0.5. API Collection (Postman)

- File Postman Collection nằm tại: `postman_collection.json` ở root project.
- Có thể import trực tiếp vào Postman để test nhanh toàn bộ luồng: catalog → cart → checkout → tracking → cancel/admin.

---

## 1. Tổng quan dự án

`HUNG HYPEBEAST Backend` là backend phục vụ cho một hệ thống bán hàng streetwear (giày, quần áo, phụ kiện) theo mô hình e-commerce chuẩn:

- Khách truy cập xem catalog sản phẩm, variant (size, màu…), tồn kho.
- Thêm sản phẩm vào giỏ hàng (theo session, không cần đăng nhập).
- Hệ thống giữ hàng tạm thời bằng cơ chế **Reservation + Inventory Locking**.
- Checkout với nhiều phương thức thanh toán (COD, SePay).
- Gửi email xác nhận đơn hàng và cung cấp link **theo dõi đơn** (tracking token).
- Admin có thể xem, lọc, cập nhật trạng thái đơn và hủy đơn.

## 2. Tech stack chính

- **Ngôn ngữ & Runtime**: Java 17
- **Framework**: Spring Boot (Web, Data JPA, Scheduling, Mail)
- **Database**: PostgreSQL
- **ORM**: Hibernate / Spring Data JPA
- **Build tool**: Maven
- **Thanh toán**: Tích hợp cổng SePay (sandbox, redirect-based flow)
- **Gửi mail**: SMTP (Gmail hoặc SMTP bất kỳ cấu hình qua `spring.mail.*`)

## 3. Các chức năng nổi bật

- **Giỏ hàng theo session**: không cần user login; backend nhận `X-Session-Id` và map với `Cart` trong DB.
- **Giữ hàng (Reservation) + Auto-Expire**:
  - Khi thêm/sửa giỏ hàng, hệ thống trừ tồn kho khả dụng và tăng tồn kho đã giữ (reserved).
  - Job `ReservationAutoExpireJob` tự động giải phóng những reservation hết hạn sau `app.reservation.ttl-minutes` phút.
- **Đơn hàng & Thanh toán**:
  - Checkout tạo `Order`, `OrderItem`, `Payment`, `TrackingToken` trong một transaction.
  - Hỗ trợ COD và SePay sandbox (redirect về `/payment/success` để cập nhật trạng thái).
- **Tự động hủy đơn PENDING**:
  - Job `OrderAutoCancelJob` định kỳ quét các đơn `PENDING` quá thời gian `app.order.auto-cancel-minutes`.
  - Gọi lại `OrderService.cancelOrder()` để **trả tồn kho** và chuyển `status=CANCELLED` đồng nhất.
- **Theo dõi đơn bằng token**:
  - Mỗi đơn có một `TrackingToken`, gửi qua email.
  - Khách chỉ cần bấm link tracking (không đăng nhập) để xem trạng thái hiện tại của đơn.
- **Admin endpoints**:
  - Bảo vệ đơn giản bằng header `X-Admin-Token` với giá trị cấu hình trong `app.admin.api-key`.
  - Cho phép filter theo trạng thái đơn, xem chi tiết và cập nhật status.

## 4. Kiến trúc & thiết kế

- **Layered Architecture**:
  - `controller`: nhận HTTP request, validate input, trả về DTO response.
  - `service`: chứa business logic (cart, reservation, checkout, order, payment, tracking).
  - `repository`: Spring Data JPA thao tác với DB.
- **Domain rõ ràng cho e-commerce**:
  - Catalog: Category, Product, ProductVariant, ProductImage, ProductInventory.
  - Cart: Cart, CartItem, Reservation.
  - Order: Order, OrderItem, Payment, TrackingToken, EmailLog.
- **Concurrency & tồn kho**:
  - Dùng các query `findByVariantIdWithLock` ở tầng repository để khóa hàng khi giữ/huỷ/checkout.
  - Tránh over-selling khi nhiều người thao tác cùng một sản phẩm/size.
- **Background jobs (Cron)**:
  - Job auto-expire reservation.
  - Job auto-cancel order PENDING.

