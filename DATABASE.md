# Database Structure: Ocean View Resort Reservation System

Based on the system concepts mapped out in `SYSTEM.md`, the persistent storage leans on a PostgreSQL 15+ database using relational tables defined via an idempotent `schema.sql` file.

## 1. Tables Overview

### `users`
Stores authenticated staff credentials for the session-based login.
- `id` (SERIAL PRIMARY KEY)
- `username` (VARCHAR(50) UNIQUE NOT NULL)
- `password_hash` (VARCHAR(256) NOT NULL) - Stores SHA-256 hashed passwords.
- `created_at` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)

### `guests`
Stores structured guest contact information linked securely against reservations.
- `id` (SERIAL PRIMARY KEY)
- `full_name` (VARCHAR(100) NOT NULL) 
- `address` (TEXT NOT NULL)
- `contact_number` (VARCHAR(15) NOT NULL) - Enforced via length checks in application rules.
- `email` (VARCHAR(255) NOT NULL)
- `created_at` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)

### `rooms`
Defines available inventory and ties into fixed pricing configurations.
- `room_number` (VARCHAR(10) PRIMARY KEY)
- `room_type` (VARCHAR(50) NOT NULL) - Check constraints restricted to defined boundaries like "Standard Single", "Standard Double", "Deluxe Single", "Deluxe Double", "Suite".
- `capacity` (INT NOT NULL) - Evaluated against user group sizes before booking confirmation.
- `nightly_rate` (DECIMAL(10,2) NOT NULL) - Maps predictably to hotel LKR rates.

### `reservations`
The core business entity, connecting guests and rooms to specific periods.
- `reservation_number` (VARCHAR(20) PRIMARY KEY) - Auto-generated tracking ID (e.g., 'RES-00001').
- `guest_id` (INT NOT NULL) - Foreign Key to `guests(id)`.
- `room_number` (VARCHAR(10) NOT NULL) - Foreign Key to `rooms(room_number)`.
- `check_in_date` (DATE NOT NULL)
- `check_out_date` (DATE NOT NULL)
- `number_of_guests` (INT NOT NULL)
- `special_requests` (TEXT)
- `status` (VARCHAR(20) DEFAULT 'Active') - States inclusive of 'Active', 'Checked Out', 'Cancelled'.
- `cancellation_reason` (TEXT NULL)
- `created_at` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)

## 2. Constraints & Seeding Strategies
- **Data Integrity**: The core dependency chains point from `reservations` into `guests` and `rooms` directly, enforcing strict referential integrity.
- **Validation Fallback**: Logic-oriented overlaps like identical dates reserved against identical rooms fall purely on DAO data checks (`PreparedStatement` overlap queries). Constraints like `check_out_date > check_in_date` run on both the `schema.sql` (via purely defined DB Check Constraints) and java controller verifications.
- **Schema Initialization (`schema.sql`)**: 
  - Seeds a default administrative staff user credential (hashed) to bootstrap the portal authentication gracefully.
  - Automatically seeds the `rooms` table directly reflecting known facility capacities and tier attributes avoiding manual data-entry pre-launch.
