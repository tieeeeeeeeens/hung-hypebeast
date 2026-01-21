-- Demo seed data for HUNG HYPEBEAST
-- Chạy file này sau khi schema đã được Hibernate tạo ra.

INSERT INTO category (id, name, description, created_at, updated_at)
VALUES (1, 'Sneaker', 'Giày sneaker local brand', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (1, 'Jordan 1 Bred', 'Jordan 1 phối màu Bred', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (1, 1, NULL, 'https://example.com/images/jordan1-bred-1.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (1, 'J1-BRED-42', '42', 'BRED', 4500000, 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (1, 1, 10, 10, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Thêm thêm category

INSERT INTO category (id, name, description, created_at, updated_at)
VALUES (2, 'Hoodie', 'Áo hoodie local brand', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO category (id, name, description, created_at, updated_at)
VALUES (3, 'Tee', 'Áo thun basic', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO category (id, name, description, created_at, updated_at)
VALUES (4, 'Accessory', 'Phụ kiện: nón, vớ, balo', now(), now())
ON CONFLICT (id) DO NOTHING;

-- Thêm thêm product

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (2, 'Hoodie Logo Hung', 'Hoodie in logo HUNG HYPEBEAST', 2, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (3, 'Tee Basic White', 'Áo thun trắng basic oversize', 3, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (4, 'Cap Classic Black', 'Nón lưỡi trai đen classic', 4, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Thêm thêm variants + inventory cho từng product

-- Jordan 1 Bred thêm size khác
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (2, 'J1-BRED-41', '41', 'BRED', 4500000, 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (2, 2, 8, 8, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Hoodie Logo Hung
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (3, 'HD-HUNG-BLACK-M', 'M', 'BLACK', 650000, 2, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (4, 'HD-HUNG-BLACK-L', 'L', 'BLACK', 650000, 2, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (3, 3, 20, 20, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (4, 4, 15, 15, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Tee Basic White
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (5, 'TEE-WHITE-M', 'M', 'WHITE', 350000, 3, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (6, 'TEE-WHITE-L', 'L', 'WHITE', 350000, 3, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (5, 5, 30, 30, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (6, 6, 25, 25, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Cap Classic Black
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (7, 'CAP-BLACK-OS', 'OS', 'BLACK', 280000, 4, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (7, 7, 40, 40, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO category (id, name, description, created_at, updated_at)
VALUES (5, 'Jacket', 'Áo khoác, coach jacket, varsity', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO category (id, name, description, created_at, updated_at)
VALUES (6, 'Bottom', 'Quần jeans, cargo, jogger', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO category (id, name, description, created_at, updated_at)
VALUES (7, 'Sock', 'Vớ / tất sneaker', now(), now())
ON CONFLICT (id) DO NOTHING;

-- Thêm thêm product sneaker, jacket, quần, vớ

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (5, 'Jordan 1 Panda', 'Jordan 1 phối màu Panda đen trắng', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (6, 'Jordan 4 Military Black', 'Jordan 4 phối màu Military Black', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (7, 'Varsity Hung Classic', 'Áo khoác varsity HUNG HYPEBEAST bản classic', 5, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (8, 'Coach Jacket Hung', 'Coach jacket logo HUNG HYPEBEAST', 5, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (9, 'Cargo Pants Olive', 'Quần cargo màu olive form rộng', 6, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (10, 'Jeans Washed Blue', 'Quần jeans xanh wash nhẹ, form straight', 6, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (11, 'Crew Socks Logo White', 'Vớ cổ trung trắng, dệt logo HUNG HYPEBEAST', 7, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, description, category_id, created_at, updated_at)
VALUES (12, 'Crew Socks Logo Black', 'Vớ cổ trung đen, dệt logo HUNG HYPEBEAST', 7, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Bổ sung thêm hình ảnh cho product

-- Jordan 1 Bred (id = 1) thêm hình
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (2, 1, NULL, 'https://example.com/images/jordan1-bred-2.jpg', 2, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (3, 1, NULL, 'https://example.com/images/jordan1-bred-detail.jpg', 3, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Hoodie Logo Hung (product = 2)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (4, 2, NULL, 'https://example.com/images/hoodie-hung-black-front.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (5, 2, NULL, 'https://example.com/images/hoodie-hung-black-back.jpg', 2, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Tee Basic White (product = 3)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (6, 3, NULL, 'https://example.com/images/tee-basic-white-front.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (7, 3, NULL, 'https://example.com/images/tee-basic-white-detail.jpg', 2, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Cap Classic Black (product = 4)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (8, 4, NULL, 'https://example.com/images/cap-classic-black-front.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Jordan 1 Panda (product = 5)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (9, 5, NULL, 'https://example.com/images/jordan1-panda-1.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (10, 5, NULL, 'https://example.com/images/jordan1-panda-2.jpg', 2, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Jordan 4 Military Black (product = 6)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (11, 6, NULL, 'https://example.com/images/jordan4-military-black-1.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (12, 6, NULL, 'https://example.com/images/jordan4-military-black-2.jpg', 2, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Varsity Hung Classic (product = 7)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (13, 7, NULL, 'https://example.com/images/varsity-hung-classic-front.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (14, 7, NULL, 'https://example.com/images/varsity-hung-classic-back.jpg', 2, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Coach Jacket Hung (product = 8)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (15, 8, NULL, 'https://example.com/images/coach-jacket-hung-front.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (16, 8, NULL, 'https://example.com/images/coach-jacket-hung-back.jpg', 2, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Cargo Pants Olive (product = 9)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (17, 9, NULL, 'https://example.com/images/cargo-olive-full.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Jeans Washed Blue (product = 10)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (18, 10, NULL, 'https://example.com/images/jeans-washed-blue-full.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Crew Socks Logo White (product = 11)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (19, 11, NULL, 'https://example.com/images/socks-logo-white-1.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Crew Socks Logo Black (product = 12)
INSERT INTO product_image (id, product_id, variant_id, url, sort_order, created_at, updated_at)
VALUES (20, 12, NULL, 'https://example.com/images/socks-logo-black-1.jpg', 1, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Thêm variants + inventory cho các product mới

-- Jordan 1 Panda (product = 5)
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (8, 'J1-PANDA-41', '41', 'BLACK/WHITE', 4300000, 5, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (9, 'J1-PANDA-42', '42', 'BLACK/WHITE', 4300000, 5, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (10, 'J1-PANDA-43', '43', 'BLACK/WHITE', 4300000, 5, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Jordan 4 Military Black (product = 6)
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (11, 'J4-MIL-BLACK-41', '41', 'BLACK/WHITE', 5200000, 6, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (12, 'J4-MIL-BLACK-42', '42', 'BLACK/WHITE', 5200000, 6, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (13, 'J4-MIL-BLACK-43', '43', 'BLACK/WHITE', 5200000, 6, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Varsity Hung Classic (product = 7)
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (14, 'VST-HUNG-BLACK-M', 'M', 'BLACK', 890000, 7, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (15, 'VST-HUNG-BLACK-L', 'L', 'BLACK', 890000, 7, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (16, 'VST-HUNG-BLACK-XL', 'XL', 'BLACK', 890000, 7, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Coach Jacket Hung (product = 8)
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (17, 'CJ-HUNG-NAVY-M', 'M', 'NAVY', 790000, 8, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (18, 'CJ-HUNG-NAVY-L', 'L', 'NAVY', 790000, 8, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (19, 'CJ-HUNG-NAVY-XL', 'XL', 'NAVY', 790000, 8, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Cargo Pants Olive (product = 9)
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (20, 'CARGO-OLIVE-M', 'M', 'OLIVE', 650000, 9, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (21, 'CARGO-OLIVE-L', 'L', 'OLIVE', 650000, 9, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (22, 'CARGO-OLIVE-XL', 'XL', 'OLIVE', 650000, 9, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Jeans Washed Blue (product = 10)
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (23, 'JEANS-WASHED-30', '30', 'BLUE', 700000, 10, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (24, 'JEANS-WASHED-32', '32', 'BLUE', 700000, 10, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (25, 'JEANS-WASHED-34', '34', 'BLUE', 700000, 10, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Crew Socks Logo White (product = 11)
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (26, 'SOCKS-WHITE-OS', 'OS', 'WHITE', 120000, 11, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Crew Socks Logo Black (product = 12)
INSERT INTO product_variant (id, sku, size, color, price, product_id, created_at, updated_at)
VALUES (27, 'SOCKS-BLACK-OS', 'OS', 'BLACK', 120000, 12, now(), now())
ON CONFLICT (id) DO NOTHING;

-- Inventory cho các variants mới (theo pattern: id = variant_id)

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (8, 8, 12, 12, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (9, 9, 12, 12, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (10, 10, 8, 8, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (11, 11, 10, 10, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (12, 12, 10, 10, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (13, 13, 10, 10, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (14, 14, 25, 25, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (15, 15, 20, 20, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (16, 16, 15, 15, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (17, 17, 18, 18, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (18, 18, 15, 15, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (19, 19, 10, 10, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (20, 20, 20, 20, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (21, 21, 18, 18, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (22, 22, 15, 15, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (23, 23, 20, 20, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (24, 24, 18, 18, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (25, 25, 15, 15, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (26, 26, 50, 50, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_inventory (id, variant_id, total, available_quantity, reserved_quantity, sold_quantity, created_at, updated_at)
VALUES (27, 27, 50, 50, 0, 0, now(), now())
ON CONFLICT (id) DO NOTHING;
