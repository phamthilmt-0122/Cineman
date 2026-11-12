-- =====================================================================
-- CineMan System - Database Schema (Đề số 17 - Hậu tố 9)
-- Hỗ trợ chuẩn SQL (tương thích MySQL 8.x và SQLite 3.x)
-- =====================================================================

-- 1. Bảng UserAccount9: Tài khoản đăng nhập dùng chung
CREATE TABLE IF NOT EXISTS UserAccount9 (
    accountId INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    fullName VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    role VARCHAR(20) NOT NULL -- 'MANAGER', 'SALESPERSON', 'CUSTOMER'
);

-- 2. Bảng Employee9: Kế thừa UserAccount9 dành cho nhân viên
CREATE TABLE IF NOT EXISTS Employee9 (
    employeeId INTEGER PRIMARY KEY AUTOINCREMENT,
    accountId INTEGER NOT NULL UNIQUE,
    hiredDate DATE NOT NULL,
    employeeRole VARCHAR(20) NOT NULL, -- 'MANAGER', 'SALESPERSON'
    FOREIGN KEY (accountId) REFERENCES UserAccount9(accountId) ON DELETE CASCADE
);

-- 3. Bảng Customer9: Kế thừa UserAccount9 dành cho khách hàng
CREATE TABLE IF NOT EXISTS Customer9 (
    customerId INTEGER PRIMARY KEY AUTOINCREMENT,
    accountId INTEGER NOT NULL UNIQUE,
    registeredDate DATE NOT NULL,
    loyaltyPoints INTEGER DEFAULT 0,
    FOREIGN KEY (accountId) REFERENCES UserAccount9(accountId) ON DELETE CASCADE
);

-- 4. Bảng MembershipCard9: Thẻ thành viên cấp cho khách hàng
CREATE TABLE IF NOT EXISTS MembershipCard9 (
    cardId INTEGER PRIMARY KEY AUTOINCREMENT,
    customerId INTEGER NOT NULL,
    cardCode VARCHAR(50) UNIQUE,
    issuedDate DATE NOT NULL,
    expiryDate DATE NOT NULL,
    cardStatus VARCHAR(20) DEFAULT 'ACTIVE', -- 'ACTIVE', 'EXPIRED', 'LOCKED'
    FOREIGN KEY (customerId) REFERENCES Customer9(customerId) ON DELETE CASCADE
);

-- 5. Bảng Cinema9: Thông tin rạp chiếu
CREATE TABLE IF NOT EXISTS Cinema9 (
    cinemaId INTEGER PRIMARY KEY AUTOINCREMENT,
    cinemaName VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL
);

-- 6. Bảng ScreeningRoom9: Phòng chiếu thuộc rạp
CREATE TABLE IF NOT EXISTS ScreeningRoom9 (
    roomId INTEGER PRIMARY KEY AUTOINCREMENT,
    cinemaId INTEGER NOT NULL,
    roomName VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    roomStatus VARCHAR(20) DEFAULT 'AVAILABLE',
    FOREIGN KEY (cinemaId) REFERENCES Cinema9(cinemaId) ON DELETE CASCADE
);

-- 7. Bảng Seat9: Ghế trong phòng chiếu
CREATE TABLE IF NOT EXISTS Seat9 (
    seatId INTEGER PRIMARY KEY AUTOINCREMENT,
    roomId INTEGER NOT NULL,
    rowLabel VARCHAR(2) NOT NULL,
    seatNumber INTEGER NOT NULL,
    seatType VARCHAR(20) DEFAULT 'STANDARD', -- 'STANDARD', 'VIP', 'COUPLE'
    seatStatus VARCHAR(20) DEFAULT 'AVAILABLE',
    FOREIGN KEY (roomId) REFERENCES ScreeningRoom9(roomId) ON DELETE CASCADE
);

-- 8. Bảng Movie9: Thông tin phim
CREATE TABLE IF NOT EXISTS Movie9 (
    movieId INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(200) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    durationMinutes INTEGER NOT NULL,
    description TEXT,
    movieStatus VARCHAR(20) DEFAULT 'SHOWING' -- 'SHOWING', 'COMING_SOON', 'STOPPED'
);

-- 9. Bảng Screening9: Lịch/suất chiếu
CREATE TABLE IF NOT EXISTS Screening9 (
    screeningId INTEGER PRIMARY KEY AUTOINCREMENT,
    movieId INTEGER NOT NULL,
    roomId INTEGER NOT NULL,
    startTime DATETIME NOT NULL,
    endTime DATETIME NOT NULL,
    screeningStatus VARCHAR(20) DEFAULT 'ACTIVE', -- 'ACTIVE', 'CANCELLED', 'FINISHED'
    FOREIGN KEY (movieId) REFERENCES Movie9(movieId) ON DELETE CASCADE,
    FOREIGN KEY (roomId) REFERENCES ScreeningRoom9(roomId) ON DELETE CASCADE
);

-- 10. Bảng Transaction9: Giao dịch mua vé
CREATE TABLE IF NOT EXISTS Transaction9 (
    transactionId INTEGER PRIMARY KEY AUTOINCREMENT,
    customerId INTEGER NOT NULL,
    salespersonId INTEGER NULL, -- NULL khi khách hàng mua trực tuyến
    transactionTime DATETIME NOT NULL,
    totalAmount DECIMAL(10,2) NOT NULL,
    paymentMethod VARCHAR(30) NOT NULL, -- 'ONLINE_BANKING', 'CASH', 'CREDIT_CARD', 'MOMO'
    transactionStatus VARCHAR(20) DEFAULT 'COMPLETED', -- 'COMPLETED', 'PENDING', 'CANCELLED'
    FOREIGN KEY (customerId) REFERENCES Customer9(customerId),
    FOREIGN KEY (salespersonId) REFERENCES Employee9(employeeId)
);

-- 11. Bảng Ticket9: Vé xem phim
CREATE TABLE IF NOT EXISTS Ticket9 (
    ticketId INTEGER PRIMARY KEY AUTOINCREMENT,
    transactionId INTEGER NOT NULL,
    screeningId INTEGER NOT NULL,
    seatId INTEGER NOT NULL,
    bookingTime DATETIME NOT NULL,
    ticketPrice DECIMAL(10,2) NOT NULL,
    ticketStatus VARCHAR(20) DEFAULT 'BOOKED', -- 'BOOKED', 'USED', 'CANCELLED'
    FOREIGN KEY (transactionId) REFERENCES Transaction9(transactionId) ON DELETE CASCADE,
    FOREIGN KEY (screeningId) REFERENCES Screening9(screeningId),
    FOREIGN KEY (seatId) REFERENCES Seat9(seatId),
    UNIQUE(screeningId, seatId) -- Một ghế chỉ bán một lần cho mỗi suất
);
