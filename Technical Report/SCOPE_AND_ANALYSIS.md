## Scope Dự án: HUNG HYPEBEAST E-commerce Backend

## Tính năng PHẢI làm (Must-have)

### 1. Catalog & Product Management
- Hiển thị danh sách sản phẩm (có phân trang)
- Quản lý chi tiết sản phẩm & Biến thể (Variants)
  - Ví dụ: Áo Thun Rồng có Size (M, L, XL) + Màu (Đen, Trắng)
  - Mỗi SKU tracking riêng
- Bộ lọc cơ bản:
  - Lọc theo Khoảng giá (Min-Max)
  - Lọc theo Loại sản phẩm (Category)

### 2. Shopping Cart
- Guest Cart (không cần tài khoản)
  - Thêm/Sửa/Xóa sản phẩm
  - Validate và **giữ chỗ trên tồn kho** (Reservation) ngay khi thêm/sửa số lượng
  - Lưu trữ tạm thời theo `sessionId`

### 3. Reservation System
- Cơ chế "Giữ chỗ" (Soft Reservation)
  - Khi khách thêm/sửa giỏ ở giai đoạn chuẩn bị thanh toán → trừ kho khả dụng (`available`) và tăng `reserved`, tạo bản ghi Reservation
  - Mỗi Reservation giữ hàng trong 10 phút (TTL cấu hình qua `app.reservation.ttl-minutes`)
- Xử lý tranh chấp (Concurrency) cho last item
- Job nhả hàng tự động
  - CRON job chạy mỗi 1 phút
  - Quét và cộng lại kho nếu khách không thanh toán sau 10 phút

### 4. Guest Checkout
- Cho phép đặt hàng mà không cần tài khoản, chỉ cần điền: Tên, SĐT, Địa chỉ


### 5. Payment Integration

#### COD (Cash on Delivery)
- Đặt hàng xong → Trạng thái "Đã xác nhận"
- Gửi email xác nhận

#### SePay (Chuyển khoản)
- Hiển thị QR Code để khách scan
- Hiển thị Mã nội dung chuyển khoản

### 6. Order Tracking & Notification
- Gửi Email Tracking
  - Email xác nhận đơn hàng chứa Link theo dõi
  - Không cần login để xem
- API Tra cứu đơn hàng (Public)
  - Xem trạng thái đơn dựa trên Token bí mật
  - Token được gửi qua email

### 7. Admin/Staff Features
- Xem danh sách đơn hàng
  - Danh sách đơn mới nhất đổ về
  - Hiển thị trạng thái, khách hàng, tổng tiền
- Đổi trạng thái đơn hàng
  - Nhân viên kho bấm chuyển trạng thái

---

## Tính năng NÊN làm (Nice-to-have)

### Authentication & Account Management
- Đăng nhập / Đăng ký (Member system)
- Lịch sử đơn hàng trong tài khoản cá nhân
- Persistent Cart (lưu giỏ hàng dài hạn)

### Payment Enhancement
- Tự động duyệt đơn SePay (Webhook)
  - Nhận callback từ SePay
  - Tự động update trạng thái đơn

### Product Management
- API Quản lý sản phẩm (CRUD)
  - Admin/Team quản lý thêm/sửa/xóa sản phẩm
  - Không cần manual insert vào DB