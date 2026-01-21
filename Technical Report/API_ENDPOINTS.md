# API Endpoints - HUNG HYPEBEAST

## 📋 Overview

Backend là **headless** (không auth, không user login), dùng **sessionId** gửi qua header `X-Session-Id` để gắn giỏ hàng và checkout cho khách vãng lai.

Mỗi endpoint gồm:
- **Method**: GET, POST, PUT, DELETE
- **URL**: Path đầy đủ
- **Auth**: Public (không cần token)
- **Description**: Mô tả chức năng

---

## 🔑 Session Endpoint

### GET /api/session
**Auth**: Public  
**Description**: Tạo `sessionId` mới cho khách vãng lai. FE gọi một lần, sau đó **luôn** gửi header `X-Session-Id` cho các API giỏ hàng / checkout.

**Response**: 200 OK
```json
{
  "sessionId": "sess-6f6bc7e8-1c9f-4f93-9d2d-4a4a1f1f9c7b"
}
```

---

## 🛍️ Product Endpoints

Controller: `/api/products` (ProductController)

### GET /api/products/categories
**Auth**: Public  
**Description**: Lấy danh sách category (id + tên) cho màn hình filter.

**Response**: 200 OK
```json
[
  { "id": 1, "name": "Áo Thun" },
  { "id": 2, "name": "Quần Jeans" }
]
```

---

### GET /api/products/categories/{categoryId}
**Auth**: Public  
**Description**: Lấy danh sách sản phẩm theo category, hỗ trợ filter giá & phân trang.

**Query Params**:
- `minPrice` (long, optional)
- `maxPrice` (long, optional)
- Các tham số phân trang chuẩn Spring: `page`, `size`, `sort`

**Response**: 200 OK (`Page<ProductDto>`)
```json
{
  "content": [
    {
      "id": 1,
      "name": "Áo Thun Rồng",
      "description": "...",
      "categoryId": 1,
      "categoryName": "Áo Thun",
      "variants": [
        {
          "id": 10,
          "sku": "AOTUNRONG-L-DEN",
          "size": "L",
          "color": "Đen",
          "price": 150000,
          "inventory": 5
        }
      ]
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 20 },
  "totalElements": 50
}
```

---

### GET /api/products/{productId}
**Auth**: Public  
**Description**: Lấy chi tiết 1 sản phẩm (thông tin cơ bản + danh sách variants + ảnh, tồn kho).

**Response**: 200 OK
```json
{
  "id": 1,
  "name": "Áo Thun Rồng",
  "description": "...",
  "categoryId": 1,
  "categoryName": "Áo Thun",
  "variants": [
    {
      "id": 10,
      "sku": "AOTUNRONG-L-DEN",
      "size": "L",
      "color": "Đen",
      "price": 150000,
      "inventory": 5
    }
  ]
}
```

---

## 🛒 Cart Endpoints

Controller: `/api/cart` (CartController)  
**Tất cả endpoints này yêu cầu header `X-Session-Id`**.

### GET /api/cart
**Auth**: Public (dựa trên `X-Session-Id`)  
**Description**: Lấy giỏ hàng hiện tại theo session.

**Response**: 200 OK
```json
{
  "id": 1,
  "sessionId": "sess-...",
  "items": [
    {
      "id": 1,
      "variantId": 10,
      "productName": "Áo Thun Rồng",
      "sku": "AOTUNRONG-L-DEN",
      "price": 150000,
      "quantity": 2,
      "lineTotal": 300000
    }
  ],
  "totalAmount": 300000
}
```

---

### POST /api/cart/items
**Auth**: Public  
**Description**: Thêm 1 variant vào giỏ hàng.

**Body**:
```json
{
  "variantId": 10,
  "quantity": 2
}
```

**Constraints**:
- `quantity >= 1` (validation, nếu vi phạm trả 400).

**Response**: 200 OK (`CartDto` như trên).

---

### PUT /api/cart/items/{itemId}
**Auth**: Public  
**Description**: Cập nhật số lượng 1 dòng trong giỏ.

**Body**:
```json
{
  "quantity": 3
}
```

**Response**: 200 OK (`CartDto`).

---

### DELETE /api/cart/items/{itemId}
**Auth**: Public  
**Description**: Xóa 1 dòng khỏi giỏ hàng.

**Response**: 200 OK (`CartDto` sau khi xóa).

---

### DELETE /api/cart
**Auth**: Public  
**Description**: Xóa toàn bộ giỏ hàng của session hiện tại.

**Response**: 200 OK (body rỗng).

---

## 💳 Checkout & Payment Endpoints

Hiện tại hệ thống **checkout một bước**:
- `/api/checkout` tạo Order + Payment + TrackingToken, trừ tồn kho, gửi email.
- Payment method hỗ trợ: `COD` và `SEPAY`.

### POST /api/checkout
**Auth**: Public (dùng `X-Session-Id`)  
**Description**: Tạo đơn hàng từ giỏ hiện tại, trừ tồn kho, tạo payment và gửi email.

**Body** (CheckoutRequest):
```json
{
  "fullName": "Nguyễn Văn A",
  "email": "khach@example.com",
  "phone": "0123456789",
  "address": "123 Đường ABC, Quận 1",
  "paymentMethod": "SEPAY"   // hoặc "COD"
}
```

**Response**: 200 OK (`OrderDetailDto`)
```json
{
  "id": 100,
  "orderNumber": "HH-AB12CD34",
  "status": "PENDING" ,
  "totalAmount": 350000,
  "createdAt": "2026-01-21T04:00:00",
  "trackingToken": "3a8b0c5d-...",
  "customerName": "Nguyễn Văn A",
  "customerPhone": "0123456789",
  "customerEmail": "khach@example.com",
  "customerAddress": "123 Đường ABC, Quận 1",
  "items": [
    {
      "productName": "Áo Thun Rồng",
      "sku": "AOTUNRONG-L-DEN",
      "quantity": 2,
      "price": 150000,
      "lineTotal": 300000
    }
  ],
  "payment": {
    "method": "SEPAY",
    "status": "PENDING_QR",
    "transactionId": null,
    "sePayReference": "HH-AB12CD34"
  }
}
```

**Ghi chú SePay**:
- Backend tạo order + payment và lưu `sePayReference = orderNumber`.
- FE/hoặc endpoint `/sepay/test-form` sẽ dùng `orderNumber` + cấu hình SePay để tạo **form POST** đến `https://pay-sandbox.sepay.vn/v1/checkout/init` (flow chuẩn của cổng thanh toán SePay).

---

## 🔍 Order Tracking (Public)

### GET /api/orders/track
**Auth**: Public (dựa trên tracking token)  
**Description**: Xem trạng thái đơn hàng qua token trong email.

**Query Params**:
- `token` (string, bắt buộc)

**Response**: 200 OK (`OrderTrackingDto`)
```json
{
  "token": "3a8b0c5d-...",
  "orderNumber": "HH-AB12CD34",
  "status": "PENDING",
  "totalAmount": 350000,
  "createdAt": "2026-01-21T04:00:00",
  "customerName": "Nguyễn Văn A",
  "items": [
    {
      "productName": "Áo Thun Rồng",
      "sku": "AOTUNRONG-L-DEN",
      "quantity": 2,
      "price": 150000
    }
  ]
}
```

---

## ❌ Public Order Cancel

### POST /api/orders/{orderId}/cancel
**Auth**: Public
**Description**: Hủy đơn hàng.

**Response**: 200 OK (`OrderDetailDto` sau khi hủy)

---

## 📊 Admin Order Endpoints

Controller: `/api/admin/orders` (AdminOrderController)  
### GET /api/admin/orders
**Auth**: Header `X-Admin-Token` (API key)  
**Description**: Danh sách đơn hàng, filter theo status, phân trang.

**Query Params**:
- `status` (string, optional): `PENDING`, `PAID`, `SHIPPING`, `DELIVERED`, `CANCELLED`, ...
- Các param phân trang chuẩn: `page`, `size`, `sort`

**Response**: 200 OK (`Page<OrderSummaryDto>`)
```json
{
  "content": [
    {
      "id": 100,
      "orderNumber": "HH-AB12CD34",
      "customerPhone": "0123456789",
      "customerName": "Nguyễn Văn A",
      "totalAmount": 350000,
      "status": "PENDING",
      "paymentMethod": "SEPAY",
      "createdAt": "2026-01-21T04:00:00"
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 20 },
  "totalElements": 1
}
```

---

### GET /api/admin/orders/{orderId}
**Auth**: Header `X-Admin-Token` (API key)  
**Description**: Xem chi tiết 1 đơn hàng (bao gồm payment + trackingToken).

**Response**: 200 OK (`OrderDetailDto`).

---

### PUT /api/admin/orders/{orderId}/status
**Auth**: Header `X-Admin-Token` (API key)  
**Description**: Cập nhật trạng thái đơn. Nếu đơn đã `CANCELLED` thì không cho đổi sang trạng thái khác.

**Query Param**:
- `status` (string, bắt buộc): giá trị của enum `OrderStatus`.

**Response**: 200 OK (`OrderDetailDto` sau khi cập nhật).

---

## 📧 Admin Email Log Endpoints

Controller: `/api/admin/email-logs` (AdminEmailLogController)

### GET /api/admin/email-logs
**Auth**: Header `X-Admin-Token` (API key)  
**Description**: Xem lịch sử gửi email (xác nhận đơn, tracking) để debug.

**Query Params**:
- `orderId` (long, optional): filter theo 1 đơn cụ thể.
- Phân trang chuẩn: `page`, `size`, `sort`.

**Response**: 200 OK (`Page<EmailLogDto>`)
```json
{
  "content": [
    {
      "id": 1,
      "orderId": 100,
      "type": "ORDER_CONFIRMATION",
      "recipientEmail": "khach@example.com",
      "status": "SENT",
      "errorMessage": null,
      "createdAt": "2026-01-21T04:00:01"
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 20 },
  "totalElements": 1
}
```

---

## 🌐 SePay Test Helper (Dev only)

### GET /sepay/test-form
**Auth**: Dev-only  
**Description**: Endpoint tiện lợi để test flow SePay sandbox. Từ `orderNumber`, backend build form đúng chuẩn và auto-submit tới `https://pay-sandbox.sepay.vn/v1/checkout/init`.

**Query Params**:
- `orderNumber` (string, bắt buộc): mã đơn ví dụ `HH-AB12CD34`.

**Response**: 200 OK (HTML auto-submit form). Khi mở trên browser, bạn sẽ được chuyển sang trang thanh toán SePay sandbox.

---

## 🌐 Payment Result Pages (SePay redirect)

Các endpoint này dùng làm `success_url`, `error_url`, `cancel_url` trong cấu hình SePay.

### GET /payment/success
**Description**: Trang thông báo thanh toán thành công (HTML đơn giản).

### GET /payment/error
**Description**: Trang thông báo thanh toán lỗi.

### GET /payment/cancel
**Description**: Trang thông báo người dùng đã hủy thanh toán.

---

## ⚠️ Error Handling (Common)

Tất cả lỗi được trả về dạng JSON chuẩn:

```json
{
  "error": "BAD_REQUEST",
  "message": "Chi tiết lỗi cụ thể",
  "status": 400,
  "timestamp": "2026-01-21T04:00:00",
  "path": "/api/checkout"
}
  "retry_after": 300
}
```

---

## 📝 Notes

- Tất cả timestamp dùng **ISO 8601 format**: `2026-01-12T10:10:00Z`
- Tất cả price dùng **bigint** (VND): không cần decimal
- Token để trong header: `Authorization: Bearer <token>`
- CORS enabled cho frontend