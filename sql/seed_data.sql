-- Seed Data cho hệ thống CineMan (Đề số 17)

-- 1. UserAccount9
INSERT INTO UserAccount9 (accountId, username, password, fullName, email, phone, status, role) VALUES
(1, 'manager', '123456', 'Trần Thị B', 'manager@cineman.com', '0901234567', 'ACTIVE', 'MANAGER'),
(2, 'sales', '123456', 'Lê Văn C', 'sales@cineman.com', '0912345678', 'ACTIVE', 'SALESPERSON'),
(3, 'customer', '123456', 'Nguyễn Văn A', 'nguyenvana@gmail.com', '0987654321', 'ACTIVE', 'CUSTOMER'),
(4, 'leminhc', '123456', 'Lê Minh C', 'leminhc@gmail.com', '0971122334', 'ACTIVE', 'CUSTOMER'),
(5, 'phamthud', '123456', 'Phạm Thu D', 'phamthud@gmail.com', '0963344556', 'ACTIVE', 'CUSTOMER'),
(6, 'hoangvane', '123456', 'Hoàng Văn E', 'hoangvane@gmail.com', '0955566778', 'ACTIVE', 'CUSTOMER');

-- 2. Employee9
INSERT INTO Employee9 (employeeId, accountId, hiredDate, employeeRole) VALUES
(1, 1, '2023-01-15', 'MANAGER'),
(2, 2, '2024-03-01', 'SALESPERSON');

-- 3. Customer9
INSERT INTO Customer9 (customerId, accountId, registeredDate, loyaltyPoints) VALUES
(1, 3, '2025-01-10', 125),
(7, 4, '2025-02-14', 87),
(12, 5, '2025-05-20', 54),
(20, 6, '2025-08-01', 18);

-- 4. MembershipCard9
INSERT INTO MembershipCard9 (cardId, customerId, cardCode, issuedDate, expiryDate, cardStatus) VALUES
(1, 1, 'CARD-KH001-2025', '2025-01-10', '2027-01-10', 'ACTIVE'),
(2, 7, 'CARD-KH007-2025', '2025-02-14', '2027-02-14', 'ACTIVE');

-- 5. Cinema9
INSERT INTO Cinema9 (cinemaId, cinemaName, address) VALUES
(1, 'CineMan Vincom Cinema Center', 'Tầng 5, TTTM Vincom, 191 Bà Triệu, Hà Nội');

-- 6. ScreeningRoom9
INSERT INTO ScreeningRoom9 (roomId, cinemaId, roomName, capacity, roomStatus) VALUES
(1, 1, 'Phòng 1 (IMAX)', 30, 'AVAILABLE'),
(2, 1, 'Phòng 2 (Standard)', 30, 'AVAILABLE'),
(3, 1, 'Phòng 3 (VIP)', 20, 'AVAILABLE');

-- 7. Seat9 (Khởi tạo ghế cho Phòng 1)
INSERT INTO Seat9 (roomId, rowLabel, seatNumber, seatType, seatStatus) VALUES
(1, 'A', 1, 'STANDARD', 'AVAILABLE'), (1, 'A', 2, 'STANDARD', 'AVAILABLE'), (1, 'A', 3, 'STANDARD', 'AVAILABLE'), (1, 'A', 4, 'STANDARD', 'AVAILABLE'), (1, 'A', 5, 'STANDARD', 'AVAILABLE'),
(1, 'B', 1, 'STANDARD', 'AVAILABLE'), (1, 'B', 2, 'STANDARD', 'AVAILABLE'), (1, 'B', 3, 'STANDARD', 'AVAILABLE'), (1, 'B', 4, 'STANDARD', 'AVAILABLE'), (1, 'B', 5, 'STANDARD', 'AVAILABLE'),
(1, 'C', 1, 'VIP', 'AVAILABLE'), (1, 'C', 2, 'VIP', 'AVAILABLE'), (1, 'C', 3, 'VIP', 'AVAILABLE'), (1, 'C', 4, 'VIP', 'AVAILABLE'), (1, 'C', 5, 'VIP', 'AVAILABLE'),
(1, 'D', 1, 'VIP', 'AVAILABLE'), (1, 'D', 2, 'VIP', 'AVAILABLE'), (1, 'D', 3, 'VIP', 'AVAILABLE'), (1, 'D', 4, 'VIP', 'AVAILABLE'), (1, 'D', 5, 'VIP', 'AVAILABLE'),
(2, 'A', 1, 'STANDARD', 'AVAILABLE'), (2, 'A', 2, 'STANDARD', 'AVAILABLE'), (2, 'A', 3, 'STANDARD', 'AVAILABLE'), (2, 'A', 4, 'STANDARD', 'AVAILABLE'),
(2, 'B', 1, 'VIP', 'AVAILABLE'), (2, 'B', 2, 'VIP', 'AVAILABLE'), (2, 'B', 3, 'VIP', 'AVAILABLE'), (2, 'B', 4, 'VIP', 'AVAILABLE');

-- 8. Movie9 (Đúng dữ liệu theo Hình 3 và 4 trong đề bài)
INSERT INTO Movie9 (movieId, title, genre, durationMinutes, description, movieStatus) VALUES
(101, 'Avengers: Endgame', 'Hành động', 181, 'Các Avengers còn lại tập hợp lần cuối để đảo ngược hành động của Thanos và cứu vũ trụ.', 'SHOWING'),
(102, 'Avengers: Infinity War', 'Hành động', 149, 'Biệt đội siêu anh hùng đối đầu Thanos khi hắn tìm kiếm 6 viên đá vô cực.', 'SHOWING'),
(103, 'The Avengers', 'Viễn tưởng', 143, 'Trận chiến New York lịch sử thành lập biệt đội siêu anh hùng đầu tiên.', 'COMING_SOON'),
(104, 'Avatar: The Way of Water', 'Khoa học viễn tưởng', 192, 'Jake Sully cùng gia đình khám phá đại dương Pandora để bảo vệ hành tinh.', 'SHOWING'),
(105, 'Lật Mặt 7: Một Điều Ước', 'Gia đình, Tâm lý', 138, 'Câu chuyện cảm động về tình mẹ và tình cảm anh em trong gia đình đông con.', 'SHOWING');

-- 9. Screening9
INSERT INTO Screening9 (screeningId, movieId, roomId, startTime, endTime, screeningStatus) VALUES
(201, 101, 1, '2026-10-20 19:00:00', '2026-10-20 22:01:00', 'ACTIVE'),
(202, 101, 2, '2026-10-21 20:30:00', '2026-10-21 23:31:00', 'ACTIVE'),
(203, 102, 1, '2026-10-22 18:00:00', '2026-10-22 20:29:00', 'ACTIVE'),
(204, 104, 2, '2026-10-22 21:00:00', '2026-10-23 00:12:00', 'ACTIVE'),
(205, 105, 3, '2026-10-23 19:30:00', '2026-10-23 21:48:00', 'ACTIVE');

-- 10. Transaction9 (Khớp theo Hình 7 & Hình 8 trong đề bài)
-- KH001 (Customer ID = 1): Nguyễn Văn A có 4-5 giao dịch từ 01/10/2026 đến 15/10/2026 tổng 1.250.000 VNĐ
INSERT INTO Transaction9 (transactionId, customerId, salespersonId, transactionTime, totalAmount, paymentMethod, transactionStatus) VALUES
(301, 1, NULL, '2026-10-03 18:20:00', 360000.00, 'Trực tuyến', 'COMPLETED'),
(315, 1, 2,    '2026-10-06 09:45:00', 180000.00, 'Tại quầy',   'COMPLETED'),
(342, 1, NULL, '2026-10-11 20:10:00', 540000.00, 'Trực tuyến', 'COMPLETED'),
(350, 1, 2,    '2026-10-14 15:30:00', 170000.00, 'Tại quầy',   'COMPLETED'),

-- KH007 (Customer ID = 7): Lê Minh C: 3 giao dịch = 870.000 VNĐ
(360, 7, NULL, '2026-10-04 14:00:00', 290000.00, 'Trực tuyến', 'COMPLETED'),
(361, 7, 2,    '2026-10-08 19:15:00', 290000.00, 'Tại quầy',   'COMPLETED'),
(362, 7, NULL, '2026-10-12 21:00:00', 290000.00, 'Trực tuyến', 'COMPLETED'),

-- KH012 (Customer ID = 12): Phạm Thu D: 2 giao dịch = 540.000 VNĐ
(370, 12, NULL, '2026-10-05 10:30:00', 270000.00, 'Trực tuyến', 'COMPLETED'),
(371, 12, 2,    '2026-10-10 16:45:00', 270000.00, 'Tại quầy',   'COMPLETED'),

-- KH020 (Customer ID = 20): Hoàng Văn E: 1 giao dịch = 180.000 VNĐ
(380, 20, 2,    '2026-10-07 11:20:00', 180000.00, 'Tại quầy',   'COMPLETED');

-- 11. Ticket9
INSERT INTO Ticket9 (ticketId, transactionId, screeningId, seatId, bookingTime, ticketPrice, ticketStatus) VALUES
(501, 301, 201, 1, '2026-10-03 18:20:00', 180000.00, 'BOOKED'),
(502, 301, 201, 2, '2026-10-03 18:20:00', 180000.00, 'BOOKED'),
(503, 315, 201, 3, '2026-10-06 09:45:00', 180000.00, 'BOOKED'),
(504, 342, 202, 1, '2026-10-11 20:10:00', 180000.00, 'BOOKED'),
(505, 342, 202, 2, '2026-10-11 20:10:00', 180000.00, 'BOOKED'),
(506, 342, 202, 3, '2026-10-11 20:10:00', 180000.00, 'BOOKED'),
(507, 350, 202, 4, '2026-10-14 15:30:00', 170000.00, 'BOOKED');
