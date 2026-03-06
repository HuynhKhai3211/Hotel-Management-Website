-- =============================================
-- SEED DATA CHO HOTEL MANAGEMENT SYSTEM
-- Chạy file này sau khi đã tạo schema từ db.sql
-- =============================================

USE HotelDB;
GO

-- =============================================
-- 1. ROLES
-- =============================================
INSERT INTO Role (role_name) VALUES
(N'Admin'),
(N'Customer'),
(N'Staff');
GO

-- =============================================
-- 2. WORK SHIFTS
-- =============================================
INSERT INTO WorkShift (shift_name, start_time, end_time) VALUES
(N'Ca sáng', '06:00', '14:00'),
(N'Ca chiều', '14:00', '22:00'),
(N'Ca đêm', '22:00', '06:00');
GO

-- =============================================
-- 3. ACCOUNTS (password: 12345678 - BCrypt hash)
-- =============================================
-- Admin account
INSERT INTO Account (email, password, full_name, phone, address, role_id, is_active) VALUES
('admin@hotel.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4dE2HBGR.b1aSGji', N'Quản trị viên', '0901234567', N'123 Đường ABC, Quận 1, TP.HCM', 1, 1);

-- Staff accounts
INSERT INTO Account (email, password, full_name, phone, address, role_id, is_active) VALUES
('staff1@hotel.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4dE2HBGR.b1aSGji', N'Nguyễn Văn An', '0912345678', N'456 Đường XYZ, Quận 2, TP.HCM', 3, 1),
('staff2@hotel.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4dE2HBGR.b1aSGji', N'Trần Thị Bình', '0923456789', N'789 Đường DEF, Quận 3, TP.HCM', 3, 1);

-- Customer accounts
INSERT INTO Account (email, password, full_name, phone, address, role_id, is_active) VALUES
('customer1@gmail.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4dE2HBGR.b1aSGji', N'Lê Văn Cường', '0934567890', N'111 Đường GHI, Quận 4, TP.HCM', 2, 1),
('customer2@gmail.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4dE2HBGR.b1aSGji', N'Phạm Thị Dung', '0945678901', N'222 Đường JKL, Quận 5, TP.HCM', 2, 1),
('customer3@gmail.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4dE2HBGR.b1aSGji', N'Hoàng Văn Em', '0956789012', N'333 Đường MNO, Quận 6, TP.HCM', 2, 1);
GO

-- =============================================
-- 4. STAFF RECORDS
-- =============================================
INSERT INTO Staff (account_id, shift_id, employee_code, hire_date) VALUES
(2, 1, 'NV001', '2024-01-15'),
(3, 2, 'NV002', '2024-03-01');
GO

-- =============================================
-- 5. CUSTOMER RECORDS
-- =============================================
INSERT INTO Customer (account_id, loyalty_points, membership_level) VALUES
(4, 100, 'Standard'),
(5, 500, 'Silver'),
(6, 1500, 'Gold');
GO

-- =============================================
-- 6. ROOM TYPES
-- =============================================
INSERT INTO RoomType (type_name, base_price, capacity, description) VALUES
(N'Phòng Standard', 500000.00, 2, N'Phòng tiêu chuẩn với đầy đủ tiện nghi cơ bản, phù hợp cho 2 người.'),
(N'Phòng Deluxe', 800000.00, 2, N'Phòng cao cấp với view đẹp, diện tích rộng rãi và tiện nghi hiện đại.'),
(N'Phòng Suite', 1500000.00, 4, N'Phòng Suite sang trọng với phòng khách riêng, phù hợp cho gia đình.'),
(N'Phòng VIP', 2500000.00, 4, N'Phòng VIP đẳng cấp nhất với dịch vụ butler 24/7 và tiện nghi 5 sao.');
GO

-- =============================================
-- 7. ROOMS
-- =============================================
INSERT INTO Room (room_number, type_id, status) VALUES
-- Standard rooms (Floor 1)
('101', 1, 'Available'),
('102', 1, 'Available'),
('103', 1, 'Occupied'),
('104', 1, 'Available'),
('105', 1, 'Maintenance'),
-- Deluxe rooms (Floor 2)
('201', 2, 'Available'),
('202', 2, 'Available'),
('203', 2, 'Occupied'),
('204', 2, 'Available'),
-- Suite rooms (Floor 3)
('301', 3, 'Available'),
('302', 3, 'Available'),
('303', 3, 'Occupied'),
-- VIP rooms (Floor 4)
('401', 4, 'Available'),
('402', 4, 'Available');
GO

-- =============================================
-- 8. AMENITIES
-- =============================================
INSERT INTO Amenity (name, icon_url) VALUES
(N'WiFi miễn phí', '/images/amenities/wifi.png'),
(N'Điều hòa', '/images/amenities/ac.png'),
(N'Minibar', '/images/amenities/minibar.png'),
(N'TV màn hình phẳng', '/images/amenities/tv.png'),
(N'Két an toàn', '/images/amenities/safe.png'),
(N'Bồn tắm', '/images/amenities/bathtub.png'),
(N'Ban công', '/images/amenities/balcony.png'),
(N'Phòng khách riêng', '/images/amenities/living.png'),
(N'Butler 24/7', '/images/amenities/butler.png'),
(N'Spa miễn phí', '/images/amenities/spa.png');
GO

-- =============================================
-- 9. ROOM TYPE - AMENITY MAPPING
-- =============================================
-- Standard: WiFi, AC, TV
INSERT INTO RoomType_Amenity (type_id, amenity_id) VALUES
(1, 1), (1, 2), (1, 4);

-- Deluxe: WiFi, AC, Minibar, TV, Safe
INSERT INTO RoomType_Amenity (type_id, amenity_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5);

-- Suite: WiFi, AC, Minibar, TV, Safe, Bathtub, Balcony, Living room
INSERT INTO RoomType_Amenity (type_id, amenity_id) VALUES
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8);

-- VIP: All amenities
INSERT INTO RoomType_Amenity (type_id, amenity_id) VALUES
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10);
GO

-- =============================================
-- 10. ROOM IMAGES
-- =============================================
INSERT INTO RoomImage (type_id, image_url) VALUES
(1, '/images/rooms/standard-1.jpg'),
(1, '/images/rooms/standard-2.jpg'),
(2, '/images/rooms/deluxe-1.jpg'),
(2, '/images/rooms/deluxe-2.jpg'),
(3, '/images/rooms/suite-1.jpg'),
(3, '/images/rooms/suite-2.jpg'),
(4, '/images/rooms/vip-1.jpg'),
(4, '/images/rooms/vip-2.jpg');
GO

-- =============================================
-- 11. VOUCHERS
-- =============================================
INSERT INTO Voucher (code, discount_amount, min_order_value, is_active) VALUES
('WELCOME10', 100000.00, 500000.00, 1),
('SUMMER20', 200000.00, 1000000.00, 1),
('VIP50', 500000.00, 2000000.00, 1),
('EXPIRED', 50000.00, 200000.00, 0);
GO

-- =============================================
-- 12. PROMOTIONS
-- =============================================
INSERT INTO Promotion (type_id, promo_code, discount_percent, start_date, end_date) VALUES
(1, 'STD10', 10.00, '2026-01-01', '2026-12-31'),
(2, 'DLX15', 15.00, '2026-01-01', '2026-06-30'),
(3, 'SUITE20', 20.00, '2026-02-01', '2026-04-30'),
(4, 'VIP25', 25.00, '2026-03-01', '2026-03-31');
GO

-- =============================================
-- 13. SAMPLE BOOKINGS
-- =============================================
INSERT INTO Booking (customer_id, room_id, voucher_id, check_in_expected, check_out_expected, total_price, status, note) VALUES
(4, 3, NULL, '2026-02-25 14:00:00', '2026-02-27 12:00:00', 1000000.00, 'Confirmed', N'Khách yêu cầu phòng hướng biển'),
(5, 8, 1, '2026-02-26 14:00:00', '2026-02-28 12:00:00', 1500000.00, 'Confirmed', NULL),
(6, 12, 2, '2026-02-27 14:00:00', '2026-03-01 12:00:00', 2800000.00, 'Pending', N'Kỷ niệm ngày cưới');
GO

-- =============================================
-- 14. OCCUPANTS
-- =============================================
INSERT INTO Occupant (booking_id, full_name, id_card_number, phone_number) VALUES
(1, N'Lê Văn Cường', '079123456789', '0934567890'),
(1, N'Nguyễn Thị Hoa', '079987654321', '0934567891'),
(2, N'Phạm Thị Dung', '079111222333', '0945678901'),
(3, N'Hoàng Văn Em', '079444555666', '0956789012'),
(3, N'Trần Thị Phương', '079777888999', '0956789013');
GO

-- =============================================
-- 15. SERVICE REQUESTS
-- =============================================
INSERT INTO ServiceRequest (booking_id, staff_id, service_type, status) VALUES
(1, 2, N'Dọn phòng', 'Completed'),
(1, NULL, N'Room service', 'Pending'),
(2, 3, N'Giặt ủi', 'In Progress');
GO

-- =============================================
-- 16. INVOICES
-- =============================================
INSERT INTO Invoice (booking_id, total_amount, tax_amount) VALUES
(1, 1100000.00, 100000.00);
GO

-- =============================================
-- 17. PAYMENTS
-- =============================================
INSERT INTO Payment (invoice_id, customer_id, payment_method, transaction_code, amount, status) VALUES
(1, 4, N'Chuyển khoản', 'TXN20260225001', 1100000.00, 'Success');
GO

-- =============================================
-- 18. FEEDBACKS
-- =============================================
INSERT INTO Feedback (booking_id, rating, comment) VALUES
(1, 5, N'Phòng sạch sẽ, nhân viên thân thiện. Sẽ quay lại!');
GO

-- =============================================
-- 19. FEEDBACK REPLIES
-- =============================================
INSERT INTO FeedbackReply (feedback_id, admin_id, reply_content) VALUES
(1, 1, N'Cảm ơn quý khách đã tin tưởng và sử dụng dịch vụ. Chúng tôi rất vui khi quý khách hài lòng!');
GO

PRINT N'Seed data đã được thêm thành công!';
GO
