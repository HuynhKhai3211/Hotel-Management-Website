

-- =============================================
-- 1. NHÓM USER & PHÂN QUYỀN
-- =============================================

CREATE TABLE Role (
    role_id INT IDENTITY(1,1) PRIMARY KEY,
    role_name NVARCHAR(50) NOT NULL UNIQUE -- Dùng NVARCHAR cho Unicode
);

CREATE TABLE Account (
    account_id INT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL, -- NVARCHAR hỗ trợ tiếng Việt
    phone VARCHAR(15),
    address NVARCHAR(255),
    role_id INT NOT NULL,
    is_active BIT DEFAULT 1, -- SQL Server dùng BIT thay cho BOOLEAN (1=True, 0=False)
    created_at DATETIME DEFAULT GETDATE(), -- Dùng GETDATE() thay cho CURRENT_TIMESTAMP
    FOREIGN KEY (role_id) REFERENCES Role(role_id)
);

CREATE TABLE WorkShift (
    shift_id INT IDENTITY(1,1) PRIMARY KEY,
    shift_name NVARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

CREATE TABLE Staff (
    account_id INT PRIMARY KEY,
    shift_id INT,
    employee_code VARCHAR(20) UNIQUE NOT NULL,
    hire_date DATE,
    FOREIGN KEY (account_id) REFERENCES Account(account_id) ON DELETE CASCADE,
    FOREIGN KEY (shift_id) REFERENCES WorkShift(shift_id)
);

CREATE TABLE Customer (
    account_id INT PRIMARY KEY,
    loyalty_points INT DEFAULT 0,
    membership_level VARCHAR(20) DEFAULT 'Standard',
    FOREIGN KEY (account_id) REFERENCES Account(account_id) ON DELETE CASCADE
);

-- =============================================
-- 2. NHÓM PHÒNG & SẢN PHẨM
-- =============================================

CREATE TABLE RoomType (
    type_id INT IDENTITY(1,1) PRIMARY KEY,
    type_name NVARCHAR(50) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    capacity INT NOT NULL,
    description NVARCHAR(MAX) -- SQL Server dùng NVARCHAR(MAX) thay cho TEXT
);

CREATE TABLE Room (
    room_id INT IDENTITY(1,1) PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    type_id INT NOT NULL,
    status NVARCHAR(20) DEFAULT 'Available',
    FOREIGN KEY (type_id) REFERENCES RoomType(type_id)
);

CREATE TABLE RoomImage (
    image_id INT IDENTITY(1,1) PRIMARY KEY,
    type_id INT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (type_id) REFERENCES RoomType(type_id) ON DELETE CASCADE
);

CREATE TABLE Amenity (
    amenity_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    icon_url VARCHAR(255)
);

CREATE TABLE RoomType_Amenity (
    type_id INT,
    amenity_id INT,
    PRIMARY KEY (type_id, amenity_id),
    FOREIGN KEY (type_id) REFERENCES RoomType(type_id) ON DELETE CASCADE,
    FOREIGN KEY (amenity_id) REFERENCES Amenity(amenity_id) ON DELETE CASCADE
);

CREATE TABLE Promotion (
    promotion_id INT IDENTITY(1,1) PRIMARY KEY,
    type_id INT NOT NULL,

promo_code VARCHAR(20) NOT NULL,
    discount_percent DECIMAL(5, 2),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    FOREIGN KEY (type_id) REFERENCES RoomType(type_id)
);

-- =============================================
-- 3. NHÓM ĐẶT PHÒNG & VẬN HÀNH
-- =============================================

CREATE TABLE Voucher (
    voucher_id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    discount_amount DECIMAL(10, 2),
    min_order_value DECIMAL(10, 2),
    is_active BIT DEFAULT 1
);

CREATE TABLE Booking (
    booking_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    room_id INT NOT NULL,
    voucher_id INT,
    booking_date DATETIME DEFAULT GETDATE(),
    check_in_expected DATETIME NOT NULL,
    check_out_expected DATETIME NOT NULL,
    check_in_actual DATETIME,
    check_out_actual DATETIME,
    total_price DECIMAL(12, 2) NOT NULL,
    status NVARCHAR(20) DEFAULT 'Pending',
    note NVARCHAR(MAX),
    FOREIGN KEY (customer_id) REFERENCES Customer(account_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id),
    FOREIGN KEY (voucher_id) REFERENCES Voucher(voucher_id)
);

CREATE TABLE Occupant (
    occupant_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    id_card_number VARCHAR(20),
    phone_number VARCHAR(15),
    FOREIGN KEY (booking_id) REFERENCES Booking(booking_id) ON DELETE CASCADE
);

CREATE TABLE ServiceRequest (
    request_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    staff_id INT,
    service_type NVARCHAR(50) NOT NULL,
    request_time DATETIME DEFAULT GETDATE(),
    status NVARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (booking_id) REFERENCES Booking(booking_id),
    FOREIGN KEY (staff_id) REFERENCES Staff(account_id)
);

-- =============================================
-- 4. NHÓM TÀI CHÍNH & PHẢN HỒI
-- =============================================

CREATE TABLE Invoice (
    invoice_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL UNIQUE,
    issued_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(12, 2) NOT NULL,
    tax_amount DECIMAL(12, 2) DEFAULT 0,
    FOREIGN KEY (booking_id) REFERENCES Booking(booking_id)
);

CREATE TABLE Payment (
    payment_id INT IDENTITY(1,1) PRIMARY KEY,
    invoice_id INT NOT NULL,
    customer_id INT NOT NULL,
    payment_method NVARCHAR(50) NOT NULL,
    transaction_code VARCHAR(100),
    amount DECIMAL(12, 2) NOT NULL,
    payment_time DATETIME DEFAULT GETDATE(),
    status NVARCHAR(20) DEFAULT 'Success',
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id),
    FOREIGN KEY (customer_id) REFERENCES Customer(account_id)
);

CREATE TABLE Feedback (
    feedback_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL UNIQUE,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    is_hidden BIT DEFAULT 0,
FOREIGN KEY (booking_id) REFERENCES Booking(booking_id)
);

CREATE TABLE FeedbackReply (
    reply_id INT IDENTITY(1,1) PRIMARY KEY,
    feedback_id INT NOT NULL UNIQUE,
    admin_id INT NOT NULL,
    reply_content NVARCHAR(MAX) NOT NULL,
    reply_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (feedback_id) REFERENCES Feedback(feedback_id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES Account(account_id)
);

