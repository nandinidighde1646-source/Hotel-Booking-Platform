# Hotel Booking Platform

A complete, runnable Java Spring Boot full-stack Hotel Booking Platform
built for an internship project. Guests can search & book rooms,
staff can check guests in/out, and admins manage rooms and see live
dashboard statistics.

## Tech Stack

- Java 17
- Spring Boot 3.3.4
- Spring Web, Spring Data JPA (Hibernate), Spring Security 6
- MySQL
- Thymeleaf + thymeleaf-extras-springsecurity6
- Jakarta Bean Validation
- Maven
- Plain HTML/CSS/JS (no frontend framework)

## Project Structure

```
src/main/java/com/hotel/hotelbooking/
├── config/          SecurityConfig, DataInitializer (sample data seeding)
├── controller/       AuthController, HomeController, RoomController,
│                     BookingController, GuestController, CheckInController,
│                     CheckOutController, AdminController, StaffController
├── dto/              RegisterDto, RoomSearchDto, BookingCreateDto, RoomDto,
│                     RoomTypeDto, DashboardStatsDto
├── model/            User, Guest, RoomType, Room, Booking, CheckIn, CheckOut
├── model/enums/      UserRole, RoomStatus, BookingStatus
├── repository/       Spring Data JPA repositories (incl. overlap-booking query)
├── service/          Business logic layer
└── exception/        ResourceNotFoundException, BookingException, GlobalExceptionHandler

src/main/resources/
├── templates/        Thymeleaf pages (index, login, register, rooms/, bookings/,
│                     guest/, checkin/, checkout/, admin/, staff/, fragments/)
└── static/           css/style.css, js/main.js
```

## 1. Prerequisites

- JDK 17+
- Maven (or use IntelliJ's bundled Maven)
- MySQL Server running locally

## 2. Database Setup

Create the database (or let the app create it automatically — the
provided JDBC URL includes `createDatabaseIfNotExist=true`):

```sql
CREATE DATABASE hotel_booking;
```

## ## 3. Configure Application Properties

1. Copy `application.properties.example` to `application.properties`.
2. Open `application.properties`.
3. Update your MySQL username and password.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_booking?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

Keep your actual database credentials private. Do not commit `application.properties` to GitHub.


```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_booking?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

Never commit your real password — `application.properties.example`
is the template that ships with the repo; keep real credentials only
in your local `application.properties`.

## 4. Open in IntelliJ IDEA

1. `File → Open` and select the project's root folder (the one with `pom.xml`).
2. Let IntelliJ auto-import the Maven project (or right-click `pom.xml` → `Add as Maven Project`).
3. Wait for dependencies to download.

## 5. Run the Application

- Run the `HotelBookingApplication` class (right-click → Run), **or**
- From a terminal in the project root: `mvn spring-boot:run`

The app starts on **http://localhost:8080**

On first run, `DataInitializer` seeds sample data automatically
(only if the database is empty):

| Role  | Email             | Password |
|-------|-------------------|----------|
| Admin | admin@hotel.com   | admin123 |
| Staff | staff@hotel.com   | staff123 |
| Guest | guest@hotel.com   | guest123 |

It also creates 4 room types (Standard, Executive, Deluxe Suite, Suite)
and 6 sample rooms (101, 102, 103, 201, 202, 203).

## 6. Try It Out

1. Go to `http://localhost:8080` — search rooms by date/guests.
2. Register a new guest account, or log in as `guest@hotel.com`.
3. Book a room — the system prevents double bookings by checking for
   overlapping non-cancelled bookings on the same room.
4. Log in as `staff@hotel.com` → **Staff Dashboard** → check guests
   in/out for today's bookings.
5. Log in as `admin@hotel.com` → **Admin Dashboard** for live stats,
   and **Manage Rooms** to add/edit rooms and room types.

## Key Business Rules Implemented

- Passwords are always BCrypt-hashed — never stored in plain text.
- Double booking is prevented at the repository/service level using
  the classic overlap check: `requestedCheckIn < existingCheckOut AND
  requestedCheckOut > existingCheckIn`, ignoring cancelled bookings.
- Check-in sets Booking → `CHECKED_IN` and Room → `OCCUPIED`.
- Check-out sets Booking → `CHECKED_OUT` and Room → `CLEANING`.
- All dashboard numbers are computed live from the database — nothing
  is hardcoded.
- Total booking amount = nights × room type's base price, computed
  server-side (never trusted from the client).

## Notes for Extending

- Add `Payment`, `Review`, or `Amenity` entities the same way the
  existing entities are structured (JPA entity → repository → service
  → controller → templates).
- `spring.jpa.hibernate.ddl-auto=update` will auto-create/update
  tables on startup — fine for a student project, but use proper
  migrations (Flyway/Liquibase) for production use.


## Task Submission

* Internship Task: [Hotel Booking Platform]
* GitHub Repository: https://github.com/nandinidighde1646-source/Hotel-Booking-Platform
* Demo Video: https://youtu.be/6Vy-kVg4SHA
