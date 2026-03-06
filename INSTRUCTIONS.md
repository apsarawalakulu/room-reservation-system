# Ocean View Resort — Reservation System
### Agent Instruction Document

---

## 1. General Instructions for the Agent

You are autonomously building a complete, working web application for the Ocean View Resort reservation system. Read this entire document before writing a single line of code.

- **Build the full project end-to-end.** Do not leave placeholder comments like `// TODO` or stub methods. Every method must be implemented and functional.
- **Write production-quality code** even though this is a university project. Proper error handling, input validation, and clear naming conventions are expected throughout.
- **Do not introduce any technology not listed in the tech stack section.** No frameworks, no ORMs, no template engines. If something is not listed, do not use it.
- **All user inputs must be validated** — both on the frontend (JS) before submission and on the backend (servlet) before any database interaction. Never trust the client.
- **Never expose raw SQL errors or stack traces to the browser.** Log them server-side and return a clean JSON error response.
- **All servlet responses must be JSON.** Servlets are purely an API layer. They do not serve HTML. HTML pages are static files that call the servlets via `fetch()`.
- **Use meaningful HTTP status codes.** `200` for success, `201` for created, `400` for bad input, `401` for auth failure, `404` for not found, `500` for server error.
- **Session-based authentication must gate every page and every servlet** except the login page and `AuthServlet`. If a user is not logged in, redirect to `index.html` from the frontend, and return `401` from the servlet.
- **The database schema must be created by a single `schema.sql` file.** It should be idempotent — use `CREATE TABLE IF NOT EXISTS`.
- **Maintain consistent code style** — 4-space indentation in Java, 2-space in HTML/CSS/JS.
- **Every DAO method must close JDBC resources** (`ResultSet`, `PreparedStatement`, `Connection`) in a `finally` block or try-with-resources. No connection leaks.
- **Commit-ready structure.** The project should be buildable and runnable by anyone who clones it, sets up the database, and configures `db.properties`, with no additional steps.

---

## 2. Project Scenario

### Background

Ocean View Resort is a popular beachside hotel located in Galle, Sri Lanka, serving hundreds of guests each month. Room reservations and guest records are currently managed manually using paper ledgers, which frequently leads to booking conflicts, double reservations, and administrative delays.

Management has commissioned a computerised web-based reservation system to replace this manual process. The system must be accessible via a web browser, require staff authentication, and support the full reservation lifecycle from booking to billing.

### Room Types and Rates

The system supports the following room categories with fixed nightly rates:

| Room Type | Nightly Rate (LKR) |
|---|---|
| Standard Single | 8,500 |
| Standard Double | 13,000 |
| Deluxe Single | 12,000 |
| Deluxe Double | 18,500 |
| Suite | 35,000 |

### Required Functionalities

#### 2.1 User Authentication
- The system must require a username and password before granting access.
- Only authenticated staff members may use the system.
- Sessions must be maintained server-side. Unauthenticated requests to any protected servlet must return HTTP 401.
- A default admin account must be seeded into the database via `schema.sql`.
- Passwords must be stored hashed (SHA-256 minimum) in the database — never plaintext.
- A logout option must be available from all pages.

#### 2.2 Add New Reservation
- Staff can register a new guest and create a reservation in a single form.
- The system must auto-generate a unique reservation number (e.g. `RES-00001`, `RES-00002`).
- Fields collected:
  - Reservation Number (auto-generated, displayed to staff after creation)
  - Guest Full Name
  - Guest Address
  - Contact Number (validated format)
  - Email Address
  - Room Type (selected from predefined list)
  - Room Number (a specific room, must not be already booked for the selected date range)
  - Check-in Date
  - Check-out Date (must be after check-in)
  - Number of Guests (must not exceed room capacity)
  - Special Requests (optional, free text)
- Validation must prevent double-booking: if the selected room is already reserved for overlapping dates, the system must reject the booking with a clear error message.

#### 2.3 View All Reservations
- A paginated, searchable table listing all reservations in the system.
- Each row must show: Reservation Number, Guest Name, Room Type, Room Number, Check-in, Check-out, Status.
- Reservations must be filterable by status (Active, Checked Out, Cancelled) and searchable by guest name or reservation number.
- Clicking a reservation opens the full detail view.

#### 2.4 View Reservation Details
- Display all stored information for a specific reservation retrieved by reservation number.
- Show computed fields: number of nights, total cost (based on room type rate × nights).
- Include action buttons: Edit Reservation, Cancel Reservation, Generate Bill.

#### 2.5 Edit Reservation
- Allow modification of guest contact details, special requests, and dates (subject to availability validation).
- Room type and room number may be changed if the new selection is available.
- Log the modification with a timestamp.

#### 2.6 Cancel Reservation
- Mark a reservation as Cancelled.
- Cancelled reservations remain in the database and visible in the all-reservations view (filterable).
- A cancellation reason field must be recorded.

#### 2.7 Calculate and Print Bill
- Generate a detailed bill for a given reservation.
- Bill must include:
  - Hotel name and address header
  - Reservation number and guest name
  - Room type, room number, nightly rate
  - Check-in and check-out dates
  - Number of nights
  - Subtotal
  - Tax (10% VAT)
  - Grand total (LKR)
  - Date the bill was generated
- The bill page must be print-friendly (use a CSS `@media print` rule that hides navigation and shows only the bill).

#### 2.8 Dashboard / Home
- After login, staff land on a dashboard showing:
  - Total reservations today
  - Currently occupied rooms count
  - Upcoming check-ins in the next 7 days
  - Recent reservations (last 5 added)
- Dashboard data is fetched from the backend via `fetch()` on page load.

#### 2.9 Help Section
- A static HTML page explaining how to use each feature of the system.
- Written for new staff members with no technical background.
- Includes a glossary of terms (reservation number, room types, etc.).

#### 2.10 Exit / Logout
- A logout button available in the navigation bar on all authenticated pages.
- Invalidates the server-side session and redirects to the login page.

### Validation Rules Summary

| Field | Rule |
|---|---|
| Guest Name | Required, letters and spaces only, 2–100 characters |
| Contact Number | Required, numeric, 10 digits |
| Email | Required, valid email format |
| Check-in Date | Required, must not be in the past |
| Check-out Date | Required, must be at least 1 day after check-in |
| Room Number | Required, must not overlap with existing active reservations |
| Number of Guests | Required, positive integer, within room capacity |
| Password (login) | Required, non-empty |

---

## 3. Technologies

| Technology | Role |
|---|---|
| Java 17 | Primary programming language for all backend logic |
| Jakarta Servlet API 6.0 | Handles HTTP requests and responses — the web service layer |
| Apache Tomcat 10.x | Servlet container that runs the application |
| JDBC (Java Database Connectivity) | Raw database access layer — all SQL is written by hand |
| PostgreSQL 15+ | Relational database storing all application data |
| Neon (or local PostgreSQL) | Hosted PostgreSQL provider (Neon free tier) or local install |
| HTML5 | Structure of all frontend pages — static files served by Tomcat |
| CSS3 | Styling of all pages, including print styles for the bill |
| Vanilla JavaScript (ES6+) | Frontend interactivity and all `fetch()` API calls to servlets |
| Gson 2.10+ | Serialising and deserialising JSON in servlets |
| dotenv-java 3.x | Loads environment variables from a `.env` file into the application at startup |
| Maven | Build tool and dependency manager — pulls in Gson, JDBC driver, Servlet API |

### What Each Technology Does in Detail

**Java 17 + Jakarta Servlets** — Each servlet maps to a URL pattern (e.g. `/api/reservations`). It reads the request body or parameters, calls the appropriate DAO, and writes a JSON response. Servlets are the only server-side entry point; there is no server-side HTML rendering.

**Apache Tomcat 10.x** — Tomcat hosts the compiled WAR file. It manages the HTTP lifecycle, hands requests to the correct servlet, and manages `HttpSession` objects for authentication. Use Tomcat 10.x specifically because it implements Jakarta EE 10 — if you use Tomcat 9.x the package names differ (`javax` vs `jakarta`).

**JDBC + PostgreSQL JDBC Driver** — `DBConnection.java` implements the Singleton pattern and holds the connection pool (or a single `Connection`). All DAO classes receive this connection and execute `PreparedStatement` queries. Never use `Statement` — always `PreparedStatement` to prevent SQL injection.

**Gson** — Used inside servlets to convert Java model objects to JSON strings for responses, and to parse incoming JSON request bodies into Java objects.

**HTML/CSS/JS (frontend)** — Pages are completely static. JavaScript `fetch()` calls hit the servlet endpoints, receive JSON, and dynamically update the DOM. No page should do a traditional HTML form `POST` submit — everything goes through `fetch()` with `Content-Type: application/json`.

**Maven** — Manages the `pom.xml`. The final build output is a `.war` file deployed to Tomcat's `webapps/` directory.

---

## 4. Recommended File Structure

```
OceanViewResort/
│
├── pom.xml                                   # Maven build file (packaging = war)
│
├── database/
│   └── schema.sql                            # All CREATE TABLE IF NOT EXISTS statements + seed data
│
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── oceanview/
        │           │
        │           ├── model/                # Plain Java POJOs — mirror database tables
        │           │   ├── User.java
        │           │   ├── Guest.java
        │           │   ├── Reservation.java
        │           │   └── Room.java
        │           │
        │           ├── dao/                  # Data Access Objects — all SQL lives here
        │           │   ├── UserDAO.java
        │           │   ├── GuestDAO.java
        │           │   ├── ReservationDAO.java
        │           │   └── RoomDAO.java
        │           │
        │           ├── servlet/              # One servlet per resource/feature
        │           │   ├── AuthServlet.java          # POST /api/auth/login, POST /api/auth/logout
        │           │   ├── ReservationServlet.java   # GET/POST/PUT /api/reservations
        │           │   ├── GuestServlet.java         # GET /api/guests
        │           │   ├── RoomServlet.java          # GET /api/rooms/available
        │           │   ├── BillServlet.java          # GET /api/bill?reservationId=
        │           │   └── DashboardServlet.java     # GET /api/dashboard
        │           │
        │           └── util/
        │               ├── DBConnection.java         # Singleton — manages JDBC connection
        │               ├── PasswordUtil.java         # SHA-256 hashing helpers
        │               ├── ValidationUtil.java       # Reusable server-side validation methods
        │               ├── JsonUtil.java             # Gson instance + helper methods
        │               └── BillCalculator.java       # Nightly rate × nights + tax logic
        │
        └── webapp/                           # Everything here is served directly by Tomcat
            │
            ├── WEB-INF/
            │   └── web.xml                   # Servlet URL mappings and session config
            │
            ├── index.html                    # Login page (the only unauthenticated page)
            ├── dashboard.html                # Home after login — summary stats
            ├── reservations.html             # All reservations — searchable, paginated table
            ├── new-reservation.html          # Add new reservation form
            ├── reservation-detail.html       # Single reservation view + actions
            ├── edit-reservation.html         # Edit reservation form
            ├── bill.html                     # Printable bill view
            └── help.html                     # Static help and guidance page
            │
            ├── css/
            │   ├── style.css                 # Global styles, layout, nav, tables, forms
            │   ├── bill.css                  # Bill-specific styles + @media print rules
            │   └── auth.css                  # Login page styles
            │
            └── js/
                ├── api.js                    # Central module — all fetch() calls, base URL config
                ├── auth.js                   # Login form handler, session check on load
                ├── dashboard.js              # Fetches and renders dashboard stats
                ├── reservations.js           # Loads reservation table, search, filter logic
                ├── new-reservation.js        # Form validation and submission
                ├── reservation-detail.js     # Loads single reservation, cancel/edit triggers
                ├── edit-reservation.js       # Pre-populates edit form, handles submission
                └── bill.js                   # Fetches bill data, renders it, triggers print
```

### Key Structural Notes for the Agent

- `DBConnection.java` must implement the **Singleton** pattern. Only one instance should exist for the lifetime of the application.
- `dao/` classes implement the **DAO (Data Access Object)** pattern. No SQL should appear anywhere outside the `dao/` package.
- `servlet/` classes implement the **MVC Controller** role. They must not contain SQL or business logic — only parse input, call DAO/util, and write JSON response.
- `util/BillCalculator.java` isolates billing logic. Servlets call it; it does not touch the database.
- `js/api.js` is the single place where the backend base URL is defined. All other JS files import from it. This makes switching between local and Neon environments a one-line change.
- Authentication check must happen at the top of every JS file (except `auth.js`) — if no valid session cookie exists, redirect to `index.html` immediately before rendering anything.
- Configuration must be loaded from a `.env` file in the project root. Use the **dotenv-java** library (`io.github.cdimascio:dotenv-java`) — add it as a Maven dependency. `DBConnection.java` must load the `.env` file at initialisation using `Dotenv.load()` and read variables from it (e.g. `DB_URL`, `DB_USER`, `DB_PASSWORD`). The `.env` file must **never** be committed to version control — add it to `.gitignore` and include a `.env.example` file with placeholder values like below:
  ```
  DB_URL=jdbc:postgresql://your-neon-host/dbname?sslmode=require
  DB_USER=your_db_user
  DB_PASSWORD=your_db_password
  ```
  No other file in the project should read from `.env` directly — all config must flow through `DBConnection.java` or a dedicated `AppConfig.java` utility that calls `Dotenv.load()` once and exposes static getters.