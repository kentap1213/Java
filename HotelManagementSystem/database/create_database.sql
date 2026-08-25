-- ============================================================
--  HOTEL MANAGEMENT SYSTEM - FULL DATABASE
-- ============================================================
--  Run this WHOLE file once in MySQL Workbench (or mysql CLI).
--  It creates the database and every table the app needs, and
--  adds sample data so you can test right away.
-- ============================================================

CREATE DATABASE IF NOT EXISTS hotel_management;
USE hotel_management;


-- ------------------------------------------------------------
-- TABLE: room_types
-- The KINDS of rooms the hotel offers (Single, Double, etc.)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS room_types (
    room_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name    VARCHAR(50) NOT NULL,
    base_price   DECIMAL(10,2) NOT NULL,   -- price per night
    description  VARCHAR(255)
);


-- ------------------------------------------------------------
-- TABLE: rooms
-- Each ACTUAL physical room in the hotel.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS rooms (
    room_id       INT AUTO_INCREMENT PRIMARY KEY,
    room_number   VARCHAR(10) NOT NULL UNIQUE,
    room_type_id  INT NOT NULL,
    floor         INT NOT NULL,
    status        ENUM('Available', 'Occupied', 'Maintenance') NOT NULL DEFAULT 'Available',
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,  -- FALSE = "deactivated" room
    FOREIGN KEY (room_type_id) REFERENCES room_types(room_type_id)
);


-- ------------------------------------------------------------
-- TABLE: guests
-- People who stay at the hotel.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS guests (
    guest_id    INT AUTO_INCREMENT PRIMARY KEY,
    first_name  VARCHAR(50) NOT NULL,
    last_name   VARCHAR(50) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    id_number   VARCHAR(50),               -- passport / government ID number
    address     VARCHAR(255)
);


-- ------------------------------------------------------------
-- TABLE: reservations
-- A booking: one guest, one room, a date range, and a status.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id  INT AUTO_INCREMENT PRIMARY KEY,
    guest_id        INT NOT NULL,
    room_id         INT NOT NULL,
    check_in_date   DATE NOT NULL,
    check_out_date  DATE NOT NULL,
    -- Booked      = reservation made, guest not arrived yet
    -- CheckedIn   = guest is currently staying in the room
    -- CheckedOut  = stay finished
    -- Cancelled   = booking was cancelled
    status          ENUM('Booked','CheckedIn','CheckedOut','Cancelled') NOT NULL DEFAULT 'Booked',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
    FOREIGN KEY (room_id)  REFERENCES rooms(room_id)
);


-- ------------------------------------------------------------
-- TABLE: payments
-- Money paid by a guest against a specific reservation.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    payment_id      INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id  INT NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    payment_date    DATE NOT NULL,
    payment_method  VARCHAR(30),           -- Cash, Card, GCash, etc.
    notes           VARCHAR(255),
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
);


-- ------------------------------------------------------------
-- TABLE: users
-- Login accounts for the app (Owner sees everything,
-- Staff has limited access).
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    role      ENUM('Owner','Staff') NOT NULL DEFAULT 'Staff'
);


-- ============================================================
-- SAMPLE DATA
-- ============================================================

INSERT INTO room_types (type_name, base_price, description) VALUES
    ('Single', 1200.00, 'One bed, fits 1 guest'),
    ('Double', 1800.00, 'Two beds, fits 2 guests'),
    ('Deluxe', 2800.00, 'Larger room with extra amenities'),
    ('Suite',  4500.00, 'Premium room with living area');

INSERT INTO rooms (room_number, room_type_id, floor) VALUES
    ('101', 1, 1),
    ('102', 1, 1),
    ('103', 2, 1),
    ('201', 2, 2),
    ('202', 3, 2),
    ('301', 4, 3);

INSERT INTO guests (first_name, last_name, phone, email, id_number, address) VALUES
    ('Juan', 'Dela Cruz', '09171234567', 'juan@example.com', 'P1234567', 'Manila'),
    ('Maria', 'Santos', '09179876543', 'maria@example.com', 'P7654321', 'Quezon City');

-- Two sample login accounts.
-- ‼ These plain-text passwords are fine for LEARNING only.
--   A real hotel system must hash passwords - see the README.
INSERT INTO users (username, password, role) VALUES
    ('admin', 'admin123', 'Owner'),
    ('staff', 'staff123', 'Staff');
