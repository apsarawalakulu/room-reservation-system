# Ocean View Resort - Unit Tests

This document tracks the implemented automated JUnit 5 / Mockito unit tests integrated into the Maven build lifecycle. These test the core business layers and servlet authentication logic of the application independently of the database.

| Test ID | Test Class | Test Method | Description | Covers Test Case | Status |
|---|---|---|---|---|---|
| UT-001 | `ValidationUtilTest` | `testValidNames()` | Validates backend checks for guest names (e.g., rejecting numbers or empty strings). | Related to TC-004 | Pass |
| UT-002 | `ValidationUtilTest` | `testValidEmail()` | Validates strict backend regex patterns for acceptable email formats. | Related to TC-004 | Pass |
| UT-003 | `ValidationUtilTest` | `testValidPhoneNumber()` | Ensures backend enforces exact 10-digit Sri Lankan phone number lengths. | Related to TC-004 | Pass |
| UT-004 | `PasswordUtilTest` | `testHashPassword()` | Verifies SHA-256 password hashing returns correct and deterministic 64-character hex strings. | Related to TC-001 | Pass |
| UT-005 | `PasswordUtilTest` | `testHashPasswordNull()` | Verifies hashing algorithm gracefully handles unexpected null password inputs. | Related to TC-002 | Pass |
| UT-006 | `BillCalculatorTest` | `testCalculateValidBill()` | Asserts correct calculation logic covering total nights multiplied by rate, plus consistent 10% VAT mapping. | TC-016 | Pass |
| UT-007 | `BillCalculatorTest` | `testCalculateSingleNightReversion()` | Tests edge case where a same-day checkout defaults backward to a minimum 1-night calculation. | TC-016 | Pass |
| UT-008 | `BillCalculatorTest` | `testCalculateMissingRoomThrowsException()` | Asserts that bill generation safely crashes via `IllegalArgumentException` if core entities are missing. | Safety logic | Pass |
| UT-009 | `AuthServletTest` | `testValidLogin()` | Utilizes Mockito to mock DAOs and servlets, verifying successful logins map `user` session attributes and output `200` JSON strings. | TC-001 | Pass |
| UT-010 | `AuthServletTest` | `testInvalidLogin()` | Utilizes Mockito to test authorization failures, verifying incorrect credentials return strict `401 Unauthorized` HTTP responses without setting sessions. | TC-002 | Pass |

### Execution
Run all integrated unit tests natively via:
`mvn clean test`
