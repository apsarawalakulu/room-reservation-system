# Ocean View Resort - Use Case Diagram

```plantuml
@startuml
left to right direction
actor "Hotel Staff" as Staff

rectangle "Ocean View Resort System" {
  usecase "Authenticate (Login/Logout)" as UC1
  usecase "View Dashboard" as UC2
  usecase "Manage Reservations" as UC3
  usecase "Add New Reservation" as UC3_1
  usecase "Edit Reservation" as UC3_2
  usecase "View All Reservations" as UC3_3
  usecase "Cancel Reservation" as UC3_4
  usecase "Generate & Print Bill" as UC4
  usecase "View Help Context" as UC5
}

Staff --> UC1
Staff --> UC2
Staff --> UC3
Staff --> UC4
Staff --> UC5

UC3_1 .u.> UC3 : <<extends>>
UC3_2 .u.> UC3 : <<extends>>
UC3_3 .u.> UC3 : <<extends>>
UC3_4 .u.> UC3 : <<extends>>
@enduml
```

A Use Case Diagram is a behavioral overview in the Unified Modeling Language (UML) that visually represents the interactions between users (actors) and an overarching system. It helps stake holders quickly grasp the primary functions the system performs without diving into technical specifics, focusing entirely on "what" the system does rather than "how" it does it. This diagram is crucial for confirming requirements and scope with non-technical stakeholders like hotel management.

This specific diagram illustrates the interactions of the single primary actor (`Hotel Staff`) with the `Ocean View Resort System` boundary. The staff member has access to several primary use cases such as Authentication, Viewing the Dashboard, Managing Reservations, Generating Bills, and Accessing the Help Menu. The Management of Reservations use case is further broken down using `<<extends>>` relationships to show optional or specific sub-actions: Adding, Editing, Viewing, and Cancelling reservations, which keeps the root tree clean while detailing the comprehensive reservation lifecycle.

The design decision here focuses on simplicity and role constraint. The system was designed strictly for internal hotel staff operation rather than public self-service. Therefore, there is no "Guest" actor interacting with the web interface. We grouped robust features like Add/Edit/Cancel underneath a single "Manage Reservations" umbrella because these functionalities share the same backend routing and validation architectures logically, making the diagram both concise and directly mapped to the software's navigation structure.
