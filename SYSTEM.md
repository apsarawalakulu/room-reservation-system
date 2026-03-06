# System Documentation: Ocean View Resort Reservation System

## 1. Overview
The Ocean View Resort Reservation System is a web-based, computerized solution designed to replace a manual, paper-ledger-based room reservation process. The system is exclusively for authenticated hotel staff to manage room availability, record guest information, process reservations, and generate billing statements.

## 2. Architecture & Tech Stack
The application follows a traditional multi-tier architecture, utilizing a purely API-driven backend and a Single Page Application (SPA)-like static frontend.

**Backend Components:**
- **Language**: Java 17
- **Server**: Apache Tomcat 10.x (Jakarta EE 10)
- **API Layer**: Jakarta Servlet API 6.0 
- **JSON Processing**: Gson 2.10+
- **Database Access**: Raw JDBC connection pool (Singleton `DBConnection`) leveraging the Data Access Object (DAO) pattern. No ORMs are used.
- **Configuration**: `dotenv-java` 3.x for environment variable management (`.env`).
- **Build Tool**: Maven (`pom.xml` outputs a `.war` file).

**Database Layer:**
- **Database**: PostgreSQL 15+ (Local or Neon hosted).
- **Schema Management**: Managed via a single, idempotent `schema.sql` file.

**Frontend Components:**
- **Structure/Styling**: HTML5 & CSS3 (including `@media print` rules for the billing view).
- **Interactivity/API calls**: Vanilla JavaScript (ES6+), exclusively using the `fetch()` API.

## 3. Core Features
- **User Authentication**: Secure, session-based staff login supporting SHA-256 password hashing. All endpoints except login and checking auth status are strictly gated.
- **Reservation Management**: 
  - Form capacity to create, view, edit, and cancel reservations.
  - Backend and frontend validations enforce strictly correctly-formatted dates, realistic occupancies (checked against room capacity), prevention of overlapping double room bookings, and structured guest detail capture.
  - System-generated reservation numbers (e.g., `RES-00001`).
- **Billing Extraction**: Generation of detailed UI bills calculating subtotal (nightly rate × nights) and 10% VAT, formatted suitably for printing.
- **Dashboard Data Feed**: Summary statistics of total reservations, occupied rooms, upcoming check-ins in the next 7 days, and 5 most recent bookings.

## 4. Room Categories & Rates
- **Standard Single**: 8,500 LKR
- **Standard Double**: 13,000 LKR
- **Deluxe Single**: 12,000 LKR
- **Deluxe Double**: 18,500 LKR
- **Suite**: 35,000 LKR
