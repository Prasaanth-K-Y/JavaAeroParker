# 🚀 E-Commerce Backend - Spring Boot Learning Project

A comprehensive Java Spring Boot backend demonstrating modern enterprise architecture patterns, security best practices, and advanced Java features.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Design Patterns](#design-patterns)
- [Java 8+ Features](#java-8-features)
- [Testing](#testing)
- [Database](#database)

---

## 🎯 Overview

This project is a **production-ready e-commerce backend** that demonstrates:

- **JWT-based Authentication & Authorization** with secure token blacklisting
- **RESTful API** design with proper HTTP status codes
- **Role-Based Access Control** (ADMIN, SELLER, CUSTOMER)
- **gRPC Integration** for microservice communication
- **JOOQ + JPA** for flexible database operations
- **Clean Architecture** with separation of concerns
- **Comprehensive Security** with Spring Security 6

### Core Functionality

✅ **User Management**: Registration, login, logout with JWT tokens
✅ **Inventory Management**: Add, update, view items
✅ **Order Processing**: Place orders with stock validation
✅ **Stock Notifications**: gRPC-based low stock alerts
✅ **Secure Logout**: Dual token blacklisting (access + refresh tokens)

---

## ✨ Features

### 🔐 Authentication & Security
- JWT-based stateless authentication
- Access tokens (15 min) & Refresh tokens (7 days)
- **Secure logout** with token blacklisting
- BCrypt password encryption
- Role-based authorization (ADMIN, SELLER, CUSTOMER)
- CSRF protection disabled for stateless API
- Token validation on every request

### 📦 Inventory Management
- CRUD operations for items
- Stock level tracking
- Low stock detection with Java Streams
- Factory pattern for item creation

### 🛒 Order Processing
- Order placement with stock validation
- Automatic stock updates
- Insufficient stock handling
- gRPC notification integration

### 🔔 Notifications (gRPC)
- Microservice communication via gRPC
- Protobuf-based message format
- Asynchronous notification dispatch

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Client Layer                        │
│            (Mobile App, Web Frontend)                   │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP/REST
┌────────────────────▼────────────────────────────────────┐
│                  Controllers                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  AuthCtrl    │  │  ECommerce   │  │  ItemView    │  │
│  │             │  │  Controller  │  │  Controller  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                   Services                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ AuthService  │  │ ItemService  │  │  JwtService  │  │
│  │             │  │             │  │             │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│                          │                              │
│                          ├──────► gRPC Client           │
└────────────────────┬─────┴─────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                  Data Access                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   JPA Repos  │  │  JOOQ DAOs   │  │  Token BL    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                  MySQL Database                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Springusers │  │    items     │  │token_blacklist│ │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack

### Core Framework
- **Spring Boot 3.2.5** - Application framework
- **Spring Security 6** - Authentication & Authorization
- **Spring Data JPA** - ORM and database operations
- **Hibernate 6.4.4** - JPA implementation

### Database
- **MySQL 8.0** - Primary database
- **JOOQ 3.18.7** - Type-safe SQL queries
- **HikariCP** - Connection pooling

### Security
- **JWT (JJWT 0.11.5)** - Token-based authentication
- **BCrypt** - Password hashing

### Microservices
- **gRPC 1.56.1** - Inter-service communication
- **Protocol Buffers 3.23.4** - Message serialization

### Build & Dependencies
- **Maven** - Build automation
- **Java 21** - Programming language

### Additional
- **Jakarta Validation** - Input validation
- **Micrometer** - Application metrics
- **JUnit 5 & Mockito** - Testing

---

## 📁 Project Structure

```
src/main/java/com/demo/ecommerce/
├── config/                          # Configuration classes
│   ├── ApplicationConfig.java       # Security beans (UserDetailsService, PasswordEncoder)
│   ├── SecurityConfig.java          # Spring Security configuration
│   ├── JooqConfig.java             # JOOQ DSL configuration
│   └── InventoryConfig.java        # Inventory thresholds
│
├── controller/                      # REST Controllers
│   ├── AuthController.java         # Authentication endpoints (login, register, logout)
│   ├── ECommerceController.java    # Item & Order management
│   └── ItemViewController.java     # Public item views (JSP)
│
├── service/                         # Business logic
│   ├── AuthService.java            # Authentication & user management
│   ├── JwtService.java             # JWT generation & validation
│   ├── ItemService.java            # Interface for item operations
│   └── ItemServiceImpl.java        # Item business logic + gRPC integration
│
├── repository/                      # Data access interfaces
│   ├── UserRepository.java         # User CRUD operations
│   ├── ItemRepository.java         # Item CRUD operations
│   └── TokenBlacklistRepository.java # Token blacklist management
│
├── dao/                            # JOOQ Data Access Objects
│   ├── ItemDao.java                # Item DAO interface
│   └── ItemDaoImpl.java            # JOOQ-based implementation
│
├── model/                          # Domain entities
│   ├── User.java                   # User entity (implements UserDetails)
│   ├── Item.java                   # Item entity
│   ├── TokenBlacklist.java         # Token blacklist entity
│   ├── Role.java                   # User roles enum
│   └── ItemFactory.java            # Factory pattern for item creation
│
├── dto/                            # Data Transfer Objects
│   ├── AuthResponse.java           # Login/Register response
│   ├── LoginRequest.java           # Login credentials
│   ├── RegisterRequest.java        # Registration data
│   ├── LogoutRequest.java          # Logout with both tokens
│   ├── RefreshTokenRequest.java    # Token refresh
│   ├── AccessTokenResponse.java    # New access token
│   └── PlaceOrderRequest.java      # Order placement
│
├── filter/                         # Request filters
│   └── JwtAuthFilter.java          # JWT validation filter
│
├── exception/                      # Custom exceptions
│   ├── GlobalExceptionHandler.java # Centralized exception handling
│   └── InsufficientStockException.java
│
└── grpc/                           # gRPC integration
    └── (auto-generated from .proto)

src/main/resources/
├── application.properties           # Configuration properties
└── proto/
    └── notification.proto           # gRPC service definition

src/test/java/
└── com/demo/ecommerce/
    └── service/
        └── ItemServiceImplTest.java # Unit tests with Mockito
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **MySQL 8.0** running on `localhost:3306`
- **gRPC Notification Server** (optional, for notifications)

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE testdb;
```

2. Update credentials in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/testdb
spring.datasource.username=root
spring.datasource.password=000000
```

3. Tables will be created automatically via Hibernate DDL:
- `Springusers` - User accounts
- `items` - Inventory items
- `token_blacklist` - Invalidated tokens

### Build & Run

```bash
# Navigate to project directory
cd c:\Users\Pky\Desktop\spring\demo

# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run
```

The server will start on **http://localhost:8080**

---

## 📡 API Endpoints

### 🔐 Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "username": "johndoe",
  "password": "securepass123",
  "address": "123 Main St",
  "role": "ROLE_CUSTOMER"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securepass123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Refresh Access Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Secure Logout
```http
POST /api/auth/logout
Content-Type: application/json

{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response:**
```
Logged out successfully
```

**Note:** Both tokens are blacklisted and cannot be used again.

---

### 📦 Item Management

#### Add Item (ADMIN/SELLER only)
```http
POST /api/items
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "itemId": "ITEM001",
  "name": "Laptop",
  "quantity": 50,
  "price": 999.99
}
```

#### Get Item
```http
GET /api/items/{itemId}
Authorization: Bearer <access-token>
```

#### Get All Items
```http
GET /api/items
Authorization: Bearer <access-token>
```

---

### 🛒 Order Management

#### Place Order (ADMIN/CUSTOMER only)
```http
POST /api/orders
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "itemId": "ITEM001",
  "quantity": 5
}
```

**Success Response:**
```
Order placed successfully for 5 units of ITEM001
```

**Insufficient Stock Response:**
```
Insufficient stock for item ITEM001. Available: 2, Requested: 5
```

---

### 👀 Public Endpoints

#### View Items (JSP Page)
```http
GET /items/view
```

Renders a JSP page displaying all items (no authentication required).

---

## 🔒 Security

### Authentication Flow

1. **Register/Login** → Receive access token & refresh token
2. **Make Request** → Include `Authorization: Bearer <access-token>` header
3. **Token Validation** → JwtAuthFilter validates token on every request
4. **Authorization** → Spring Security checks user roles
5. **Logout** → Both tokens blacklisted in database

### Token Details

| Token Type | Lifetime | Purpose |
|------------|----------|---------|
| Access Token | 15 minutes | API authentication |
| Refresh Token | 7 days | Generate new access tokens |

### Security Features

✅ **Stateless Authentication** - No server-side sessions
✅ **Token Blacklisting** - Revoked tokens cannot be reused
✅ **Password Encryption** - BCrypt hashing (cost factor 10)
✅ **Role-Based Access** - Method-level security with `@PreAuthorize`
✅ **CSRF Protection** - Disabled for stateless API
✅ **JWT Signature** - HS256 with 256-bit secret key

### Role Permissions

| Endpoint | ADMIN | SELLER | CUSTOMER |
|----------|-------|--------|----------|
| `POST /api/items` | ✅ | ✅ | ❌ |
| `POST /api/orders` | ✅ | ❌ | ✅ |
| `GET /api/items` | ✅ | ✅ | ✅ |
| `POST /api/auth/*` | ✅ | ✅ | ✅ |

---

## 🎨 Design Patterns

### 1. **Factory Pattern**
```java
public class ItemFactory {
    public static Item create(String id, String name, int qty, double price) {
        return new Item(id, name, qty, price);
    }
}
```

### 2. **Strategy Pattern**
Different inventory strategies can be implemented via `ItemService` interface.

### 3. **Repository Pattern**
Data access abstracted through Spring Data JPA repositories.

### 4. **DTO Pattern**
Separate request/response objects from domain entities.

### 5. **Builder Pattern**
JWT token building with fluent API.

### 6. **Filter Chain Pattern**
Request processing through security filter chain.

---

## ☕ Java 8+ Features

### Lambda Expressions
```java
.filter(item -> item.getQuantity() < lowStockThreshold)
```

### Stream API
```java
return itemDao.findAll().stream()
    .filter(item -> item.getQuantity() < 5)
    .sorted(Comparator.comparingInt(Item::getQuantity))
    .toList();
```

### Method References
```java
.sorted(Comparator.comparingInt(Item::getQuantity))
```

### Optional API
```java
userRepository.findByUsername(username)
    .orElseThrow(() -> new IllegalArgumentException("User not found"));
```

### Record Classes (Java 14+)
```java
public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}
```

### LocalDateTime API
```java
LocalDateTime expiresAt = expiration.toInstant()
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime();
```

---

## 🧪 Testing

### Unit Tests with JUnit 5 & Mockito

```java
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemDao itemDao;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testGetLowStockItems() {
        // Arrange
        List<Item> mockItems = Arrays.asList(
            new Item("1", "Item1", 3, 10.0),
            new Item("2", "Item2", 8, 20.0)
        );
        when(itemDao.findAll()).thenReturn(mockItems);

        // Act
        List<Item> result = itemService.getLowStockItems();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Item1", result.get(0).getName());
    }
}
```

### Test Coverage

- ✅ Service layer unit tests
- ✅ Mocked dependencies
- ✅ AAA (Arrange-Act-Assert) pattern
- ✅ Edge case testing

### Running Tests

```bash
mvn test
```

---

## 🗄️ Database

### Entity Relationships

```
User (1) ──────────────────┐
                           │
                           │ has role
                           │
                           ▼
                        Role (enum)
                    (CUSTOMER, ADMIN, SELLER)

Item (*)
  │
  └──► ItemDao (JOOQ)
  └──► ItemRepository (JPA)

TokenBlacklist (*)
  │
  └──► Stores invalidated JWT tokens
```

### JPA Configuration

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### JOOQ Integration

Type-safe SQL queries for complex operations:

```java
dsl.insertInto(table("items"))
   .set(field("item_id"), item.getItemId())
   .set(field("name"), item.getName())
   .execute();
```

---

## 🔗 gRPC Integration

### Notification Service

**Proto Definition:**
```protobuf
service NotificationService {
  rpc NotifyInsufficientStock(NotificationRequest) returns (NotificationResponse);
}

message NotificationRequest {
  string item_id = 1;
  int32 available_quantity = 2;
  int32 requested_quantity = 3;
}
```

**Usage:**
```java
NotificationRequest grpcRequest = NotificationRequest.newBuilder()
    .setItemId(itemId)
    .setAvailableQuantity(available)
    .setRequestedQuantity(requested)
    .build();

notificationStub.notifyInsufficientStock(grpcRequest);
```

---

## 🧩 Dependency Injection

### Spring CDI Equivalents

| CDI Annotation | Spring Equivalent | Usage |
|----------------|-------------------|-------|
| `@Inject` | `@Autowired` | Constructor/field injection |
| `@Named` | `@Service` / `@Component` | Bean naming |
| `@SessionScoped` | `@SessionScope` | Session-scoped beans |
| `@ApplicationScoped` | `@ApplicationScope` | Singleton beans |

### Example:

```java
@Service("fastItemService")
@SessionScope
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }
}
```

---

## 📚 Additional Documentation

- **[Logout Feature Guide](LOGOUT_FEATURE.md)** - Detailed logout implementation
- **[Security Enhancement](SECURITY_ENHANCEMENT.md)** - Token blacklisting security

---

## 🏁 Conclusion

This project demonstrates a **production-ready Spring Boot backend** with:

✅ **Modern Security** - JWT authentication with token blacklisting
✅ **Clean Architecture** - Layered design with separation of concerns
✅ **Enterprise Patterns** - Factory, Strategy, Repository, DTO
✅ **Microservices** - gRPC integration for inter-service communication
✅ **Type Safety** - JOOQ for compile-time SQL validation
✅ **Best Practices** - Input validation, exception handling, logging
✅ **Testable Code** - Unit tests with Mockito

Perfect for learning Spring Boot, security best practices, and enterprise Java development.

---

## 📝 License

This is a learning project for educational purposes.

## 👤 Author

Developed as a comprehensive Spring Boot learning project demonstrating enterprise-grade patterns and security practices.
