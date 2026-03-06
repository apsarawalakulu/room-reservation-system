CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(256) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS guests (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rooms (
    room_number VARCHAR(10) PRIMARY KEY,
    room_type VARCHAR(50) NOT NULL CHECK (room_type IN ('Standard Single', 'Standard Double', 'Deluxe Single', 'Deluxe Double', 'Suite')),
    capacity INT NOT NULL,
    nightly_rate DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    reservation_number VARCHAR(20) PRIMARY KEY,
    guest_id INT NOT NULL REFERENCES guests(id),
    room_number VARCHAR(10) NOT NULL REFERENCES rooms(room_number),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INT NOT NULL,
    special_requests TEXT,
    status VARCHAR(20) DEFAULT 'Active' CHECK (status IN ('Active', 'Checked Out', 'Cancelled')),
    cancellation_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_dates CHECK (check_out_date > check_in_date)
);

-- Seed an admin user
-- Username: admin
-- Password: admin123 (SHA-256 hash provided below)
INSERT INTO users (username, password_hash)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9')
ON CONFLICT (username) DO NOTHING;

-- Seed default rooms
INSERT INTO rooms (room_number, room_type, capacity, nightly_rate) VALUES
('101', 'Standard Single', 1, 8500.00),
('102', 'Standard Single', 1, 8500.00),
('103', 'Standard Single', 1, 8500.00),
('201', 'Standard Double', 2, 13000.00),
('202', 'Standard Double', 2, 13000.00),
('203', 'Standard Double', 2, 13000.00),
('301', 'Deluxe Single', 1, 12000.00),
('302', 'Deluxe Single', 1, 12000.00),
('303', 'Deluxe Double', 2, 18500.00),
('304', 'Deluxe Double', 2, 18500.00),
('401', 'Suite', 4, 35000.00),
('402', 'Suite', 4, 35000.00)
ON CONFLICT (room_number) DO NOTHING;
