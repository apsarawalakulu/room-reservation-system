# Ocean View Resort - Reservation Management System

A complete hotel room reservation management system built with Java EE (Jakarta Servlets 6.0) and Tomcat.

## 🚀 Features
- **Frontend**: Responsive, premium static HTML/CSS/Vanilla JS UI connecting entirely via RESTful APIs.
- **Backend API**: Pure Java 17 servlets utilizing Jakarta EE without heavy frameworks. 
- **Database**: PostgreSQL integrations with pure JDBC PreparedStatement calls wrapped within generic Data Access Objects (DAOs). 
- **Security**: Complete Session-based Authentication guarding all functionality, backed by native SHA-256 password hashing.
- **Configuration**: `dotenv-java` implementing modular environment setups separating credentials cleanly out of root source scopes.

---

## 🛠️ Project Setup Instructions

### Prerequisites
Before you begin, ensure you have the following installed on your machine:
- **Java 17 JDK** or higher
- **Apache Tomcat 10.x** (Required for Jakarta EE 10 / Servlet API 6.0 support)
- **Apache Maven 3.8+**
- **PostgreSQL 15+** (Local installation or hosted instance e.g., Neon)

### 1. Database Configuration
1. Start your PostgreSQL server and create a new database for the project (e.g., `oceanview`).
2. Execute the initialization schema located at `OceanViewResort/database/schema.sql` against your new database. This will create the necessary tables (`users`, `rooms`, `guests`, `reservations`) and seed data (including the default `admin` user and initial room inventory).
   ```bash
   psql -U your_postgres_user -d oceanview -f OceanViewResort/database/schema.sql
   ```

### 2. Environment Variables (.env File)
The application connects to the database utilizing a `.env` configuration file injected during runtime via `dotenv-java`.

1. Navigate to the inner `OceanViewResort` directory:
   ```bash
   cd OceanViewResort
   ```
2. Create a file named `.env` in this directory. 
3. Copy the template variables from `.env.example` and replace the credentials with your local PostgreSQL parameters.
   ```env
   # Example local configurations
   DB_URL=jdbc:postgresql://localhost:5432/oceanview?user=postgres&password=root
   ```
*(Note: Do not commit the `.env` file to version control. It is already mapped within `.gitignore` for safety.)*

### 3. Build the Project
Use Apache Maven to compile the Java source code and build the deployable `.war` (Web Application Archive) output:
```bash
cd OceanViewResort
mvn clean package
```
*If everything is configured correctly, this will finish successfully and generate a `OceanViewResort.war` file inside the `OceanViewResort/target` folder.*

### 4. Deploy to Apache Tomcat
1. Copy the generated `OceanViewResort.war` file from the `target/` directory into your Tomcat `webapps/` directory.
   ```bash
   cp target/OceanViewResort.war /path/to/tomcat/webapps/
   ```
2. Start the Tomcat Server. Tomcat will automatically extract the war file and spin up the servlet contexts.
   ```bash
   /path/to/tomcat/bin/startup.sh
   # (or startup.bat on Windows)
   ```

### 5. Access the Application
1. Open a browser and navigate to the local Tomcat deployment endpoint:
   - `http://localhost:8080/OceanViewResort/`
2. You will be greeted by the Staff Portal Access login page.
3. Authenticate using the default seeded admin account:
   - **Username**: `admin`
   - **Password**: `admin123`

---

## 🧪 Testing

The system is equipped with an integrated suite of headless automated unit tests utilizing **JUnit 5** and **Mockito**. These tests thoroughly validate business logic layers (like billing algorithms and generic utilities) and directly mock request components to assess servlet execution pathways, keeping exact alignment with the scenarios documented in `TESTCASES.md`.

### How to Run Tests
Because the tests are integrated directly into the Maven build lifecycle, running the test suite requires no custom shell scripts:

1. Open your terminal in the `OceanViewResort` project directory.
2. Execute the Maven test phase command:
   ```bash
   mvn test
   ```
*(Note: Using this command isolates test execution without repackaging a full WAR. To clean the test cache and run sequentially, execute `mvn clean test`)*

---

## 🗂️ Project Structure Recap
- **`/database`**: Contains `schema.sql` for PostgreSQL setup.
- **`/src/main/java/com/oceanview`**: Contains the pure Java Backend (Models, DAOs, Servlets, and Utils).
- **`/src/main/webapp`**: Contains the static Frontend (HTML pages, CSS stylistic layers, specific Vanilla JS flow layers). 
- **`pom.xml`**: Manages backend library dependencies including Servlet API, Gson, implementation drivers, and build outputs.
