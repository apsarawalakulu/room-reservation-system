# Ocean View Resort - Test Cases

| Test Case ID | Test Case Name | Test Case Description | Main functionality Tested | Status |
|---|---|---|---|---|
| TC-001 | Valid Staff Login | Verify that a staff member can log in with correct credentials. | User Authentication | Pass |
| TC-002 | Invalid Login Credentials | Verify that login fails with an incorrect username or password. | User Authentication | Pass |
| TC-003 | Access Protected Route Without Session | Verify that unauthenticated access to system pages redirects to the login page. | User Authentication | Pass |
| TC-004 | Create Valid Reservation | Verify that a new reservation with valid details is created successfully with auto-generated ID. | Add New Reservation | Pass |
| TC-005 | Create Reservation with Past Check-in Date | Verify that creating a reservation with a past check-in date is rejected. | Add New Reservation | Pass |
| TC-006 | Create Booking with Overlapping Dates | Verify that creating a reservation for a room already booked for those dates fails. | Add New Reservation | Pass |
| TC-007 | Exceed Room Capacity | Verify that booking a room for more guests than its capacity is rejected. | Add New Reservation | Pass |
| TC-008 | View All Reservations List | Verify that all reservations are displayed in a paginated table. | View All Reservations | Pass |
| TC-009 | Filter Reservations by Status | Verify that reservations can be filtered by Active, Checked Out, and Cancelled. | View All Reservations | Pass |
| TC-010 | Search Reservations by Guest Name | Verify that searching by guest name returns the correct reservations. | View All Reservations | Pass |
| TC-011 | Search Reservations by ID | Verify that searching by reservation number returns the correct exact match. | View All Reservations | Pass |
| TC-012 | View Reservation Details | Verify that clicking a reservation displays its full details including computed costs. | View Reservation Details | Pass |
| TC-013 | Edit Guest Contact Details | Verify that a staff member can successfully update the contact details of a reservation. | Edit Reservation | Pass |
| TC-014 | Edit Room Dates to Overlapping Dates | Verify that changing dates to an already booked timeframe fails. | Edit Reservation | Pass |
| TC-015 | Cancel Reservation Successfully | Verify that an active reservation can be cancelled and a cancellation reason is recorded. | Cancel Reservation | Pass |
| TC-016 | Generate Accurate Bill | Verify that the bill accurately calculates total cost including room rate x nights and 10% VAT. | Calculate and Print Bill | Pass |
| TC-017 | Print View for Bill Page | Verify that the bill page applies `@media print` CSS rules correctly. | Calculate and Print Bill | Pass |
| TC-018 | Load Dashboard Statistics | Verify that the dashboard accurately displays total reservations, occupied rooms, and upcoming check-ins. | Dashboard / Home | Pass |
| TC-019 | Access Help Section | Verify that the static help and guidance page securely loads and displays correctly. | Help Section | Pass |
| TC-020 | Successful Logout | Verify that clicking the logout button invalidates the session and redirects to login exactly. | Exit / Logout | Pass |
