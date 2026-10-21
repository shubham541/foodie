# 🍔 Foodie – House of Shukita

A pickup-only ordering platform for **House of Shukita**, built for our society: a customer storefront with the real menu and photos, and a dashboard where the restaurant owner runs everything, all backed by a secure Spring Boot API. Customers order ahead and collect from the restaurant; there is no home delivery.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen)
![MySQL](https://img.shields.io/badge/DB-MySQL%20%7C%20H2-blue)
![Tests](https://img.shields.io/badge/tests-27%20passing-success)

---

## ✨ What it does

| Who | What they can do |
|-----|------------------|
| **Customer** | Browse the 52-item menu with photos, search, filter by category and veg-only, build a cart, check out with just a contact number (cash / UPI at pickup), track the order live, get a "ready for pickup" notice, cancel before it is accepted |
| **Restaurant owner (the boss)** | Runs everything from one dashboard: live order board (New → In the kitchen → Ready for pickup → Done), accept/reject, advance orders, mark orders handed over, cancel any open order, full order history, mark dishes sold out, change prices, pause/resume incoming orders, see summary numbers and reports (average time to pickup, average order value, top dishes), activate/deactivate customers |

The restaurant, its menu, prices, photos and logo come from `src/main/resources/seed/house-of-shukita.json` and `static/images/` (source: the *Menu_21stOct* price list in `docs/house-of-shukita/`).

## 🚀 Quick start

**Prerequisites:** JDK 21 and either a local MySQL 8 *or* nothing (use the H2 profile).

```bash
# Option A – zero setup (embedded H2 database stored in ./data)
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2

# Option B – MySQL on localhost:3306 (database `shukita_foodie` is created automatically)
export FOODIE_DB_PASSWORD=your-mysql-root-password
./mvnw spring-boot:run
```

Windows: use `mvnw.cmd` and `set VAR=value` instead of `export`. Open **http://localhost:8080**.

### Demo accounts (development defaults)

| Role | Email | Password | Lands on |
|------|-------|----------|----------|
| Restaurant owner | `houseofshukita@gmail.com` | `Shukita@12345` | `/dashboard/restaurant` |
| Customer | sign up on `/register` | – | `/orders` |

> These defaults are for local development only. The `prod` profile **refuses to start** until you override them (see below).

### Try the whole flow in two browsers
1. As a customer, add dishes, check out → order appears as *Order placed*.
2. As the restaurant: **Accept → Start preparing → Mark ready**.
3. The customer's *My orders* page (it refreshes itself every few seconds) tells them to collect the order; the restaurant then taps **Handed over**.

## ⚙️ Configuration

Everything is an environment variable with a safe development default (`application.properties`).

| Variable | Purpose | Default |
|----------|---------|---------|
| `FOODIE_DB_URL` / `FOODIE_DB_USER` / `FOODIE_DB_PASSWORD` | Database | local MySQL `shukita_foodie`, `root` / `password` |
| `FOODIE_JWT_SECRET` | JWT signing key (32+ chars) | dev-only value |
| `FOODIE_JWT_EXPIRY_MINUTES` | Token lifetime | `480` |
| `FOODIE_RESTAURANT_EMAIL` / `_PASSWORD` | Seeded restaurant owner | dev defaults |
| `FOODIE_DDL_AUTO` | Hibernate schema mode | `update` |
| `PORT` | HTTP port | `8080` |

### Production
```bash
export SPRING_PROFILES_ACTIVE=prod
export FOODIE_JWT_SECRET="$(openssl rand -base64 48)"
export FOODIE_RESTAURANT_PASSWORD=...
export FOODIE_DB_URL=jdbc:mysql://db-host:3306/shukita_foodie  FOODIE_DB_USER=...  FOODIE_DB_PASSWORD=...
./mvnw -DskipTests package && java -jar target/foodie-1.0.0.jar
```
The `prod` profile disables Swagger and aborts on development secrets. Serve it behind HTTPS (reverse proxy / load balancer). A `Dockerfile` and `docker-compose.yml` are included.

## 🏗️ Architecture

```
Browser (Thymeleaf shells + vanilla JS)  ──JWT──▶  REST API (/api/v1)  ──▶  Services  ──▶  JPA  ──▶  MySQL / H2
```

```
src/main/java/com/bits/foodie
├── config/       AppProperties, DataSeeder (menu + accounts), ProductionGuard, OpenApiConfig
├── security/     SecurityConfig, JwtService, JwtAuthFilter, LoginAttemptService
├── domain/       User, Restaurant, MenuItem, Order, OrderItem + enums (Role, OrderStatus, PaymentMethod)
├── repository/   Spring Data repositories
├── service/      AuthService, CatalogService, OrderService, AdminService, CustomerService
├── web/          REST controllers + PageController (HTML routes)
├── dto/          Request/response records with validation
└── exception/    ApiException + one global handler → uniform JSON errors
src/main/resources
├── seed/house-of-shukita.json     restaurant + 52 dishes
├── static/{css,js,images}         frontend assets (menu photos, logo, hero)
└── templates/{layout,pages}       Thymeleaf pages
```

### Order lifecycle
```
PLACED → ACCEPTED → PREPARING → READY_FOR_PICKUP → COLLECTED
   └────────────── CANCELLED (rules below) ──────────────┘
```

| Actor | Allowed moves |
|-------|---------------|
| Customer (own order) | cancel while `PLACED` |
| Restaurant owner | next step (accept, prepare, mark ready, mark collected) or cancel any open order, at any stage |

Steps cannot be skipped, finished orders cannot change, and concurrent updates to the same order are rejected (optimistic locking).

### Pricing
Prices are stored **excluding GST** (as in the price list). The server computes `subtotal + 5% GST` from the database on every order; the client never sends prices. Order lines are snapshots, so later menu edits don't alter past orders.

## 🔐 Security
- Stateless **JWT** auth (HS256); role and active flag are re-checked from the database on every request, so deactivating a user takes effect immediately.
- **RBAC** by URL (`/restaurant-admin`, `/customers`) plus ownership checks in services (customers only see their own orders).
- Passwords hashed with **BCrypt**; policy: 8–72 chars with a letter and a number.
- Brute-force lockout (5 failures → 15 minutes), identical error for unknown email vs wrong password.
- Self-registration always creates a customer; the restaurant owner account is provisioned from configuration.
- Server-side validation on every request; no stack traces or internals in error responses; all HTML output escaped in the UI; open-redirect-safe `next=` handling.
- No cookies are used, so CSRF is not applicable (documented in `SecurityConfig`).

## 📡 API
Interactive docs (dev): **http://localhost:8080/swagger-ui.html**. All errors look like `{timestamp, status, error, message, path, fieldErrors?}`.

| Area | Endpoints |
|------|-----------|
| Auth | `POST /api/v1/auth/register`, `POST /auth/login`, `GET /auth/me` |
| Public | `GET /api/v1/restaurants`, `/restaurants/{id}`, `/restaurants/{id}/menu` |
| Customer | `GET·PATCH /customers/me` |
| Orders | `POST /orders`, `GET /orders/mine`, `GET /orders/{id}`, `PATCH /orders/{id}/status`, `POST /orders/{id}/cancel` |
| Restaurant owner | `GET /restaurant-admin/restaurant`, `/orders` (`?filter=ALL` for history), `/menu`, `/summary`, `/reports`, `/customers`; `PATCH /restaurant-admin/menu/{itemId}`, `/settings`, `/customers/{id}/active` |
| Health | `GET /actuator/health` |

## 🧪 Tests
```bash
./mvnw test
```
27 tests: unit tests (JWT, order state machine) and full-stack integration tests on in-memory H2 covering auth, validation, lockout, RBAC, server-side pricing, the complete order lifecycle, cancellation rules, sold-out dishes, paused ordering, the legacy-delivery database migration, owner summary/reports/customer management and page/asset serving.

## 📝 Notes for the restaurant owner
- **Veg / non-veg labels**: dishes with egg are marked non-veg. *The Full House Breakfast* and *Gym Bro Combo* have no label because their contents aren't in the price list – set `"veg": true/false` for them in the seed JSON (or tell us).
- **Photos**: most dishes use the restaurant's own photos. Where none existed, a similar stock photo from Wikimedia Commons is used (credited on the `/credits` page, source list in `docs/house-of-shukita/IMAGE_CREDITS.md`) – replace them with your own whenever you like. Three combos still show the logo placeholder. Drop new images into `static/images/menu/` and reference them in the seed JSON (`"image": "file.jpg"`); dishes that have no photo yet pick it up on the next start.
- **Menu arrangement**: Maggi and Wai Wai lead (spicy versions first, tagged ⭐ Bestseller), then combos, healthy breakfast/bowls, eggs, sandwiches, parathas and rotis. Dishes carry `spicy`, `bestseller` and `healthy` tags in the seed JSON, shown as badges, a Bestsellers strip and filter chips. Order and tags always follow the seed file.
- **Seed behaviour**: on every start only *missing* dishes are added; prices/availability changed from the dashboard are never overwritten (menu order and tags are re-applied from the seed file).
- **Opening hours and the shop address** are not in the price list, so no hours are enforced – use the *Accepting orders* switch.

## 🔄 Upgrading an older database
On start-up the app migrates a database created by the earlier delivery/admin version: delivery-partner and admin accounts, the address book and the delivery columns are removed, and old orders are mapped onto the pickup statuses (`DELIVERED` → `COLLECTED`, `OUT_FOR_DELIVERY` → `READY_FOR_PICKUP`). It is a no-op on a fresh or already-migrated database.

## 🗺️ Not included yet
Online payment gateway (payment is at pickup), SMS/email notifications, multiple restaurants in the UI (the data model already supports them), password reset, restaurant onboarding screen.
