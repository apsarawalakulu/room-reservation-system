# Ocean View Resort - Sequence Diagram

```plantuml
@startuml
actor "Hotel Staff" as Staff
participant "Frontend (JS/HTML)" as UI
participant "AuthFilter" as Filter
participant "ReservationServlet" as Servlet
participant "ReservationDAO" as DAO
participant "PostgreSQL DB" as DB

Staff -> UI : Fills Reservation Form & Submits
UI -> Filter : POST /api/reservations (JSON fetch)
activate Filter
Filter -> Filter : Validate Session Cookie Status
Filter -> Servlet : Forward Authorized Request
activate Servlet
Servlet -> Servlet : Parse JSON to Reservation Object
Servlet -> DAO : createReservation(reservationObj)
activate DAO
DAO -> DB : SELECT (Check dates for overlaps)
activate DB
DB --> DAO : Return Available
DAO -> DB : INSERT INTO reservations
DB --> DAO : Success (Generated RES- ID)
deactivate DB
DAO --> Servlet : Return filled Reservation mapping
deactivate DAO
Servlet --> Filter : 201 Created (JSON Response)
deactivate Servlet
Filter --> UI : 201 Created (JSON Response Object)
deactivate Filter
UI -> Staff : Show Success Msg & New ID
@enduml
```

A Sequence Diagram is an interaction UML diagram that focuses on the chronological progression of messages passed between system components. It details the step-by-step control flow and data transitions across the timeline of a specific scenario or use case. These diagrams are critical for understanding how different tiers of an architecture (frontend, controllers, services, databases) synchronize to fulfill a user request, helping uncover potential bottlenecks or logical gaps in complex workflows before they are built.

This diagram visualizes the "Add New Reservation" process. It spans the entire application stack horizontally from the actor (`Hotel Staff`) to the final persistent storage node (`PostgreSQL DB`). It maps the flow of a staff member submitting a frontend form: the execution trickles through the vanilla JavaScript `fetch` handler, is intercepted by the Java `AuthFilter` to verify session status, progresses to the `ReservationServlet` for JSON extraction, flows down to the `ReservationDAO` layer, and executes a two-stage SQL interaction (SELECT verification then INSERT) against the actual database before successfully returning up the chain. 

The sequence clearly demonstrates a deliberate design decision segregating responsibilities. Instead of embedding SQL securely within the servlet, we isolate data interactions into a `DAO` (Data Access Object) tier. Furthermore, an `AuthFilter` intercepts the request initially instead of placing session-checking logic inside the servlet directly. This promotes the DRY (Don't Repeat Yourself) principle and adheres to the Model-View-Controller (MVC) API paradigm outlined in the project instructions, ensuring a clean separation of concerns and a highly visible security perimeter.
