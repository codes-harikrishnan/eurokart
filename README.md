# EuroKart — E-Commerce Backend API

A production-quality e-commerce REST API built with Java Spring Boot, demonstrating senior-level backend engineering practices including domain-driven design, JWT authentication, Redis caching, async processing, and comprehensive test coverage.

**Live Demo:** _[Add Render URL after deployment]_  
**GitHub:** https://github.com/codes-harikrishnan/eurokart

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 24 |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | PostgreSQL 18 |
| Caching | Redis + Spring Cache |
| Migrations | Flyway |
| Async Processing | Spring @Async + ThreadPoolTaskExecutor |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito, AssertJ |
| Containerisation | Docker |
| Deployment | Render |

---

## Architecture

### Package Structure
Domain-driven vertical slice architecture — each domain owns its controller, service, repository, and DTOs:

```
com.harikrishnan.eurokart
├── category/         # Category management
├── order/            # Order placement, status lifecycle, history
│   ├── controller
│   ├── domain
│   ├── dto
│   ├── enums
│   ├── repository
│   └── service
├── product/          # Product catalog with Redis caching
├── user/             # Authentication and user management
├── configuration/    # Security, JWT filter, CORS, async config
├── exception/        # Global exception handling
└── util/             # JWT service, notification service, security utils
```

### Key Patterns
- **Rich Domain Model** — business invariants enforced inside entities (e.g. `product.deductStock()` validates stock before deducting)
- **JWT Authentication** — stateless authentication via `OncePerRequestFilter`, token validated on every request
- **Redis Caching** — `@Cacheable` on product lookups, `@CacheEvict` on mutations, verified with Redis CLI
- **Async Notifications** — `@Async` email notifications after order placement, MDC context propagated across thread boundaries
- **MDC Request Tracing** — unique `requestId` attached to every request via `LogFilter`, visible in all log lines
- **Specification API** — dynamic order filtering by status and date range without multiple repository methods
- **SecurityUtils** — authenticated user extracted from `SecurityContextHolder` rather than trusting client-supplied user IDs

---

## Features

### Authentication
- `POST /user/register` — register a new user, async welcome notification fired
- `POST /user/authenticate` — login, returns JWT token

### Category Management
- `POST /category` — create category
- `GET /category` — list all categories
- `GET /category/{id}` — get category by id
- `PUT /category/{id}` — update category
- `DELETE /category/{id}` — delete category

### Product Catalog
- `POST /products` — create product
- `GET /products` — list all products
- `GET /products/{id}` — get product by id (Redis cached)
- `PUT /products/{id}` — update product (cache evicted)
- `DELETE /products/{id}` — delete product (cache evicted)

### Order Management
- `POST /order/make` — place order (transactional — validates stock, deducts inventory, fires async notification)
- `GET /order/allOrders` — order history with pagination and filtering by status and date range
- `PATCH /order/{id}/status` — update order status (Admin only)
- `PATCH /order/{id}/cancel` — cancel order (owner or admin, only PENDING orders)

---

## API Endpoints Reference

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/user/register` | Register new user | Public |
| POST | `/user/authenticate` | Login, get JWT token | Public |
| POST | `/category` | Create category | Required |
| GET | `/category` | List all categories | Required |
| GET | `/category/{id}` | Get category by id | Required |
| PUT | `/category/{id}` | Update category | Required |
| DELETE | `/category/{id}` | Delete category | Required |
| POST | `/products` | Create product | Required |
| GET | `/products` | List all products | Required |
| GET | `/products/{id}` | Get product by id | Required |
| PUT | `/products/{id}` | Update product | Required |
| DELETE | `/products/{id}` | Delete product | Required |
| POST | `/order/make` | Place order | Required |
| GET | `/order/allOrders` | Order history (paginated + filtered) | Required |
| PATCH | `/order/{id}/status` | Update order status | Admin only |
| PATCH | `/order/{id}/cancel` | Cancel order | Required |

---

## Running Locally

### Prerequisites
- Java 24
- Maven 3.9+
- Docker Desktop

### 1. Start Infrastructure

```bash
# PostgreSQL
docker run -d \
  --name eurokart-postgres \
  -e POSTGRES_USER=eurokart_user \
  -e POSTGRES_PASSWORD=eurokart_pass \
  -e POSTGRES_DB=eurokartdb \
  -p 5433:5432 \
  postgres

# Redis
docker run -d -p 6379:6379 --name eurokart-redis redis
```

### 2. Configure Environment

Update `src/main/resources/application.yml` with your local values or set environment variables:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/eurokartdb
    username: <your-db-username>
    password: <your-db-password>
  data:
    redis:
      host: localhost
      port: 6379
jwt:
  secret: <your-secret-key>
  expiration: 86400000
```

Generate a secret key:
```bash
openssl rand -base64 32
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`

---

## Order Placement Flow

```
POST /order/make
  → Validate authenticated user (from JWT, not request body)
  → For each order item:
      → Fetch product
      → product.deductStock(quantity)  ← Rich Domain Model
      → Calculate item total
  → Save Order with PENDING status
  → Save OrderItems
  → Fire async notification (separate thread, MDC context propagated)
  → Return OrderResponseDto
  → If any step fails → full rollback (@Transactional)
```

---

## Testing

```bash
mvn test
```

**51 tests — 0 failures**

| Test Type | Coverage |
|-----------|----------|
| Unit tests (`@ExtendWith(MockitoExtension)`) | Service layer — all business logic paths |
| Slice tests (`@WebMvcTest`) | Controller layer — HTTP contracts, validation, security |
| Repository tests (`@DataJpaTest`) | Specification-based queries against H2 |
| JWT tests | Token generation, extraction, expiry |

Key scenarios covered:
- Happy paths and all failure paths (not found, conflict, insufficient stock, unauthorised)
- Stock deduction verified via entity state assertion
- MDC context propagation verified in async tests
- Specification filters verified against real in-memory database

---

## Security Design

- **Stateless JWT** — no server-side sessions, token validated on every request
- **User identity from token** — authenticated user extracted from `SecurityContextHolder`, never trusted from request body
- **Role-based access** — `@PreAuthorize("hasRole('ADMIN')")` on admin-only endpoints
- **Ownership enforcement** — users can only cancel their own orders, verified against authenticated identity
- **Password hashing** — BCrypt via Spring Security `PasswordEncoder`

---

## Deployment

Deployed on Render with managed PostgreSQL and Redis add-ons.

**Live URL:** _[Add after deployment]_

### Environment Variables on Render

| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | PostgreSQL connection string |
| `SPRING_DATASOURCE_USERNAME` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `SPRING_DATA_REDIS_URL` | Redis connection string |
| `JWT_SECRET` | JWT signing secret |
| `JWT_EXPIRATION` | Token expiry in milliseconds |

---

## What This Project Demonstrates

- Production-quality Spring Boot backend built from scratch
- Domain-driven package structure with vertical slice development
- Rich Domain Model — entities own their business invariants
- JWT authentication implemented from first principles
- Redis caching with proper eviction strategy
- Async processing with MDC context propagation across thread boundaries
- Comprehensive test coverage across all layers
- Flyway database migrations
- Deployed and accessible via live URL
