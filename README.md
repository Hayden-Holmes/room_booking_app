# Room Booking Application

## Overview
This application allows users to search for rooms, view availability, and create reservations. It enforces booking constraints such as required date/time inputs and building availability, while providing a simple UI for navigation and filtering.

---

## Helpful Notes

- Default test user:
  - Username: `testUser`
  - Password: `testPassword`

- Seed data:
  - Reservations are preloaded for **April 30th**
  - Use this date to test availability and booking behavior


## Core Functionalities

- Authentication: user login and logout  
- Session Routing: redirect after login and booking actions  
- Navigation: access dashboard and move between pages  
- Search: perform room searches  
- Filtering: refine results by amenities, building, availability  
- Sorting: order results (e.g., by capacity)  
- Room Details: view detailed room information including reservations for a given day  
- Booking: create reservations with required date/time inputs and sends an email notification (This only works locally as free render version does not support SMPT on port 587)
- Reservation Management: view and cancel bookings  
- Notifications & UX: email confirmations, performance, and usability  

---

## Tech Stack

- Backend: Spring Boot  
- Persistence: Spring Data JPA  
- Database: SQLite  
- Frontend: Thymeleaf, HTML, Inline CSS  
- Build Tool: Maven  
- CI/CD: GitHub Actions  

---

## Architecture / Coding Logic

### Presentation Layer
- Located in: `controller/` and `templates/`
- Handles routing, user input, and UI rendering  
- `search.html` performs **client-side filtering** after results are returned  
  - Filtering is not treated as core business logic  

### Business Logic Layer
- Located in: `service/` and `model/`
- Services handle:
  - Room search
  - Booking
  - Cancellation
  - Dashboard logic  
- Model classes:
  - Represent entities
  - Include core business rules (e.g., room availability, building open status)  
- `RoomSearchService` uses **RoomSpecifications** for dynamic query construction  

### Data Layer
- Located in: `data/`
- Uses Spring Data JPA repositories for database interaction  

### Configuration
- `config/` provides necessary beans (usedd for testing)

### Data Initialization
- `DataLoader.java` loads seed data from the `seed/` directory at startup  

## Performance Test Summary

**What the test does**
- Runs 2 scenarios using k6:
  - 1 user → baseline response time
  - 1000 users → load/stress test

**Where to find test and results**
- `perfomance/load-test.java`
- `perfomance/test-results.png`



**Pass / Fail**
- ✅ Response time (single user): PASS (p95 = 76ms < 1000ms)
- ❌ Failure rate: FAIL (85.36% > 1% threshold)

**Why it failed**
- System cannot handle 1000 concurrent users
- Requests are timing out or being dropped (not reaching checks)
- Likely bottlenecks:
  - SQLite (poor concurrency)
  - Limited server resources (Render free tier)
