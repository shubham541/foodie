# 🍔 Foodie – Food Delivery Platform

Foodie is a Spring Boot backend (with a few server-rendered Thymeleaf pages) for an online food-delivery platform. It supports four user roles – **Customer**, **Restaurant**, **Delivery Person** and **Admin** – covering registration/login, restaurant discovery, cart & ordering, order fulfilment and platform administration.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen)
![MySQL](https://img.shields.io/badge/DB-MySQL-blue)
![Build](https://img.shields.io/badge/Build-Maven-red)

---

## 📑 Table of Contents
1. [Tech Stack](#-tech-stack)
2. [Features Implemented](#-features-implemented)
3. [Architecture & Project Structure](#-architecture--project-structure)
4. [Data Model](#-data-model)
5. [Order Lifecycle](#-order-lifecycle)
6. [REST API Reference](#-rest-api-reference)
7. [Validation & Error Handling](#-validation--error-handling)
8. [Security](#-security)
9. [Getting Started](#-getting-started)
10. [Testing](#-testing)
11. [Known Limitations / Roadmap](#-known-limitations--roadmap)

---

## 🧰 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language / Runtime | Java 21 |
| Framework | Spring Boot 3.3.4 (Web, Data JPA, Security, Thymeleaf) |
| Database | MySQL (via `mysql-connector-j`), Hibernate ORM |
| API Docs | springdoc-openapi 2.2.0 (Swagger UI) |
| Validation | Jakarta Bean Validation 3.0.2 |
| Boilerplate | Lombok |
| Password hashing | BCrypt (`BCryptPasswordEncoder`) |
| Testing | JUnit 5, Mockito 5, Spring Boot Test, Surefire |
| Build | Maven (wrapper included) |

---

## ✅ Features Implemented

### 👤 Customer
- Register and log in (BCrypt-hashed passwords)
- Update profile, saved addresses and saved payment cards
- Fetch customer by ID
- Browse all restaurants
- Search restaurants by **name**, **cuisine type**, **veg-only** flag and **food item name**
- Create/update cart (items + quantities, tied to a restaurant)
- Place an order from the cart (cart is moved into order history)
- Track order status (`orderId → status`)
- View order history

### 🍽️ Restaurant
- Register and log in
- Update restaurant details (cuisine, veg-only, opening/closing hours, delivery zones)
- Fetch restaurant by ID
- View and update the **menu** (items with price, description, availability)
- View incoming orders and update order status
- Deactivate a restaurant

### 🛵 Delivery Person
- Register and log in
- Update profile (contact details, vehicle type)
- Toggle availability
- View available deliveries (orders in `Order Placed` state)
- Update the status of an order being delivered
- Fetch by ID, deactivate

### 🛡️ Admin
- Register and log in
- Create users of any role (`/createUser`)
- Update or deactivate a Customer / Restaurant / Delivery Person
- View all orders, **cancel** an order, **reschedule** (change status of) an order
- **Reports** (`/generateReports`): most popular restaurant, fastest-delivery restaurant, most-ordered item
- **Platform activity dashboard** (`/platformActivity`): active users, ongoing deliveries, total / pending / completed orders

### 🌐 Web Pages (Thymeleaf)
- Homepage, Login, Register and catalogue templates with shared header/footer fragments and custom CSS

### ⚙️ Cross-cutting
- Centralised exception handling (`@ControllerAdvice`) with consistent `ResponseDTO` bodies
- Input validation annotations on request DTOs
- DTO ↔ entity mapping layer (`mappers`)
- Swagger / OpenAPI documentation
- Unit tests for all controllers and service implementations

---

## 🏗️ Architecture & Project Structure

Classic layered architecture: **Controller → Service → Repository → Entity**, with DTOs and mappers isolating the API from the persistence model.

```
foodie
├── pom.xml
├── mvnw / mvnw.cmd
└── src
    ├── main
    │   ├── java/com/bits/foodie
    │   │   ├── FoodieApplication.java
    │   │   ├── controller/     # Admin, Customer, DeliveryPerson, Restaurant, Home
    │   │   ├── service/        # Service interfaces + SecurityConfig, SwaggerConfig
    │   │   │   └── impl/       # Business logic implementations
    │   │   ├── repository/     # Spring Data JPA repositories
    │   │   ├── model/          # JPA entities
    │   │   ├── dto/            # DTOs
    │   │   │   ├── payload/    # LoginRequestDTO, RegisterRequestDTO
    │   │   │   └── response/   # ResponseDTO, PlatformActivityDTO
    │   │   ├── mappers/        # Entity <-> DTO mappers
    │   │   ├── exception/      # Custom exceptions + GlobalExceptionHandler
    │   │   └── utility/        # FoodieConstants (roles, order statuses)
    │   └── resources
    │       ├── application.properties
    │       ├── static/css/styles.css
    │       └── templates/      # Login, Register, homepage, catalogue, fragments/
    └── test/java/com/bits/foodie
        ├── FoodieApplicationTests.java
        └── test
            ├── controller/     # 4 controller test classes
            └── service/        # 4 service test classes
```

`HomeController` acts as the single entry point for **role-based registration and login** (`/api/v1/save`, `/api/v1/login`) and delegates to the role-specific controllers.

---

## 🗄️ Data Model

```
Customer 1──* Address
Customer 1──* Card
Customer 1──1 Cart 1──* CartItem *──1 Item
Customer 1──* Order 1──1 Cart
Restaurant 1──1 Menu 1──* Item
Restaurant *──* deliveryZones (element collection)
Admin, DeliveryPerson (standalone)
```

| Entity | Key fields |
|--------|-----------|
| `Customer` | name, unique email, password, role, isActive, addresses, saved cards, cart, order history |
| `Restaurant` | name, unique email, address, cuisineType, isVegOnly, opening/closing hours, deliveryZones, menu, isActive, orderCount |
| `DeliveryPerson` | name, unique email, contactDetails, vehicleType, devAvailable, isActive |
| `Admin` | name, unique email, role |
| `Menu` / `Item` | menu → items; item has name, price, description, isAvailable |
| `Cart` / `CartItem` | restId, total price, items with quantity |
| `Order` | cart, restaurantId, amount, status, isCancelled, deliveryTimeInMinutes, customer |
| `Address` / `Card` | customer-owned address and payment card (`YearMonth` expiry) |

---

## 🔄 Order Lifecycle

Statuses are defined in `FoodieConstants`:

```
Order Placed  →  Preparing  →  Picked Up  →  En Route  →  Delivered
```

- **Customer** places the order (`Order Placed`)
- **Restaurant** / **Delivery Person** / **Admin** advance the status through the update endpoints
- **Admin** can cancel an order at any time

---

## 📡 REST API Reference

Base path: `/api/v1`. Interactive docs: **http://localhost:8080/swagger-ui.html** (OpenAPI JSON at `/v3/api-docs`).

### Authentication / Registration (`HomeController`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/save` | Register a `customer`, `deliveryperson` or `restaurant` (by `role`) |
| `POST` | `/api/v1/login` | Log in as one of those roles |

### Customer – `/api/v1/customer`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `PATCH` | `/` | Update customer |
| `GET` | `/id/{customerId}` | Get customer |
| `GET` | `/restaurants` | List all restaurants |
| `GET` | `/restaurants/search` | Search (`restaurantName`, `cuisineType`, `isVegOnly`, `foodItemName`) |
| `POST` | `/id/{customerId}/updateCart` | Update cart |
| `POST` | `/id/{customerId}/placeOrder` | Place order from cart |
| `GET` | `/id/{customerId}/trackOrders` | Order id → status map |
| `GET` | `/id/{customerId}/viewOrderHistory` | Past orders |

### Restaurant – `/api/v1/restaurant`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | List restaurants |
| `GET` | `/{restaurantId}` | Get restaurant |
| `PATCH` | `/{restaurantId}` | Update restaurant |
| `GET` | `/{restaurantId}/menu` | Get menu |
| `PATCH` | `/{restaurantId}/menu` | Update menu |
| `GET` | `/{restaurantId}/viewOrders` | View orders |
| `PATCH` | `/{restaurantId}/order/{orderId}/updateOrderStatus?status=` | Update order status |
| `GET` | `/search` | Search restaurants |
| `DELETE` | `/{restaurantId}/deactivate` | Deactivate |

### Delivery Person – `/api/v1/deliveryPerson`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/{id}` | Get delivery person |
| `PATCH` | `/{id}` | Update profile |
| `GET` | `/viewAvailableDeliveries` | Orders awaiting pickup |
| `PATCH` | `/id/{id}/order/{orderId}/updateOrderStatus?status=` | Update order status |
| `PATCH` | `/id/{id}/manageDeliveryAvailability?availability=` | Set availability |

### Admin – `/api/v1/admin`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/save` | Register admin |
| `POST` | `/login` | Admin login |
| `POST` | `/createUser` | Create any user |
| `PATCH` | `/updateUser/{userRole}/{userId}` | Update user |
| `PATCH` | `/deactivateUser/{userRole}/{userId}` | Deactivate user |
| `GET` | `/viewAllOrders` | All orders |
| `PATCH` | `/cancelOrder/{orderId}` | Cancel order |
| `PATCH` | `/rescheduleOrder/{orderId}?status=` | Change order status |
| `GET` | `/generateReports` | Performance report |
| `GET` | `/platformActivity` | Platform activity summary |

### Web pages
`GET /`, `/home`, `/login`, `/register`

---

## 🧯 Validation & Error Handling

- **Validation**: `@NotNull`, `@Email`, `@Size` (password 6–20 chars) on `LoginRequestDTO` and `RegisterRequestDTO`.
- **Custom exceptions**: `ResourceNotFoundException`, `InvalidInputException`, `OperationFailedException`.
- **`GlobalExceptionHandler`** maps exceptions to a uniform `ResponseDTO { message, status }`:

| Exception | HTTP status |
|-----------|-------------|
| `ResourceNotFoundException`, `NoSuchElementException` | 404 |
| `InvalidInputException`, `IllegalArgumentException` | 400 |
| `OperationFailedException`, any other `Exception` | 500 |

---

## 🔐 Security

- Passwords are stored hashed with **BCrypt**.
- `SecurityConfig` defines a `SecurityFilterChain` (custom `/login` page, logout, CSRF disabled for the API) and the `PasswordEncoder` bean.
- Admin report/activity endpoints are annotated with `@PreAuthorize("hasRole('Admin')")`.

> ⚠️ See [Known Limitations](#-known-limitations--roadmap) – the current filter chain permits all requests.

---

## 🚀 Getting Started

### Prerequisites
- JDK 21
- MySQL running on `localhost:3306`
- Maven (or use the bundled wrapper)

### Configure
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/foodie?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=<your-password>
```

The `foodie` database is created automatically if it does not exist.

### Run
```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

The app starts at **http://localhost:8080**.

### Example requests
```bash
# Register a customer
curl -X POST http://localhost:8080/api/v1/save \
  -H "Content-Type: application/json" \
  -d '{"role":"customer","customerName":"Asha","customerEmail":"asha@example.com","password":"secret123"}'

# Login
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"role":"customer","customerEmail":"asha@example.com","password":"secret123"}'

# Search restaurants
curl "http://localhost:8080/api/v1/customer/restaurants/search?cuisineType=Italian&isVegOnly=true"
```

---

## 🧪 Testing

Unit tests use **JUnit 5 + Mockito** and cover every controller and service implementation (76 test methods across 8 classes).

```bash
./mvnw test
```

| Area | Test classes |
|------|--------------|
| Controllers | `AdminControllerTest`, `CustomerControllerTest`, `DeliveryPersonControllerTest`, `RestaurantControllerTest` |
| Services | `AdminServiceImplTest`, `CustomerServiceImplTest`, `DeliveryPersonServiceImplTest`, `RestaurantServiceImplTest` |
| Context | `FoodieApplicationTests` |

---

## 🚧 Known Limitations / Roadmap

These are observations from the current codebase, listed so they can be tackled next:

- **Security is not enforced**: `SecurityConfig` permits `/**`, and the `spring.security.enabled=false` properties are not real Spring Boot 3 settings. There is no JWT/session auth, so `@PreAuthorize` has no effect in practice. Role-based authorization should be added.
- **Validation is declared but not triggered**: controllers don't use `@Valid` on request bodies.
- **Database config**: `ddl-auto=create` wipes the schema on every start, and DB credentials are committed in `application.properties` – move to environment variables / profiles and use `update` or migrations (Flyway/Liquibase).
- **Entities returned directly** from several endpoints (including password hashes) – return DTOs instead.
- `placeOrder` returns `null` for an empty/invalid cart rather than a proper 4xx error.
- Placeholder methods on some entities (e.g. `Admin.generateReports()`) are unused stubs.
- Order-status transitions are not validated against the lifecycle.
- The `catalogue.html` page is a stub; the UI is not yet wired to the REST API.

---

## 📄 License
No license specified yet – add one (e.g. MIT) before publishing publicly.
