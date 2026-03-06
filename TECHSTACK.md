# Ocean View Resort - Technology Stack

This document outlines the core technologies, frameworks, and libraries utilized to build the Ocean View Resort Reservation System. The architecture relies on a traditional Java EE stack functioning as a pure API layer, served to a decoupled static frontend.

## 1. Core Platform
| Technology | Version | Role |
|---|---|---|
| **Java** | 17+ | The primary programming language used for all backend modeling, routing, and business logic execution. Object-oriented concepts are heavily utilized to cleanly encapsulate domains (`users`, `reservations`, etc.). |
| **Jakarta Servlet API** | 6.0.0 | The core web-service API specification. Functions as the HTTP controller layer parsing incoming web requests and mapping them to respective Java executions without the overhead of heavy abstractions like Spring Boot. |
| **Apache Tomcat** | 10.x | The web application container (servlet engine). Tomcat executes the `.war` archive, manages HTTP request lifecycles, and natively handles authenticated `HttpSession` context tracking. |

## 2. Database & Persistence
| Technology | Version | Role |
|---|---|---|
| **PostgreSQL** | 15+ | The relational database management system storing all persistent application data. |
| **PostgreSQL JDBC Driver** | 42.7.2 | The native Java driver allowing the application to connect to the PostgreSQL instance. |
| **Raw JDBC** | N/A | Operates as the interaction layer. No ORM (like Hibernate) is used; all data access employs manually crafted, parameterized `PreparedStatement` queries enclosed in Data Access Objects (DAOs) to prevent SQL injection. |

## 3. Backend Utilities (Maven Dependencies)
These libraries are pulled mechanically via the `pom.xml` Maven build lifecycle:

| Dependency | Version | Role |
|---|---|---|
| **Gson** | 2.10.1 | The JSON serialization/deserialization library. Automatically parses incoming standard JavaScript `fetch()` payloads into Java POJOs (Plain Old Java Objects) and writes structured JSON output payloads back to the client. |
| **dotenv-java** | 3.0.0 | Environment management tool. Used specifically by the Singleton `DBConnection` layer to parse `.env` files safely off the local filesystem, securely decoupling secrets (like database passwords) from hardcoded version control. |

## 4. Testing Suite
| Dependency | Version | Role |
|---|---|---|
| **JUnit Jupiter (JUnit 5)** | 5.10.0 | The foundational unit-testing framework used to execute assertions against business logic. |
| **Mockito** | 5.11.0 | Testing framework used to generate mock instances of external dependencies (like `HttpServletRequest` streams or `DAO` connections) to cleanly isolate unit execution flows. |
| **Maven Surefire Plugin** | 3.1.2 | Maven build plugin responsible for discovering and executing the testing suite during the `mvn clean test` phases. |

## 5. Frontend Technologies
The frontend is built entirely using natively executed technologies decoupled from server-side rendering processes (like JSP). 

| Technology | Role |
|---|---|
| **HTML5** | Sets the strict structural semantic foundations of all the web pages. Pages are inherently static files retrieved via the base URL routing. |
| **CSS3** | Implements robust visual designs recursively. Features modularity (e.g., dividing global styles from printable bill rules like `@media print`) without external utility abstractions like Tailwind or Bootstrap. |
| **Vanilla JavaScript (ES6+)** | Provides all dynamic functionality. Single-handedly responsible for gathering HTML form data, initiating async `fetch()` HTTP network calls to the underlying Java API, and directly mutating the Document Object Model (DOM) to display reservations natively. |
