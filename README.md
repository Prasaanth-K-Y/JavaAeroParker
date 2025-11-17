# JavaAeroParker Project

A Java Spring Boot + JOOQ + DAO + gRPC Learning Project

---

#  **JavaAeroParker**

JavaAeroParker is a learning-oriented backend project demonstrating:

* **Spring Boot**
* **DAO + JOOQ**
* **gRPC communication**
* **Java 8 Lambdas & Streams**
* **Design patterns (Strategy, Builder, Factory)**
* **CDI concepts (Spring equivalents)**

The project simulates a simple **Inventory + Order Processing** backend where:

* Items can be added
* Users can place orders
* System checks stock
* A **gRPC notification** is sent when stock is insufficient
* DAO layer is implemented using **JOOQ**

---

# 🗂️ **Project Structure**

```
src/
 └── main/java/com/demo/ecommerce/
      ├── controller/
      │     └── ECommerceController.java
      ├── service/
      │     ├── ItemService.java
      │     └── ItemServiceImpl.java
      ├── dao/
      │     ├── ItemDao.java
      │     └── ItemDaoImpl.java
      ├── model/
      │     └── Item.java
      ├── dto/
      │     └── PlaceOrderRequest.java
      ├── grpc/
            └── NotificationService (auto-generated)
```

---

# 🚀 **1. Java 8+ Features Used**

## ✔️ 1.1 Lambda Expressions

Used in **ItemServiceImpl#getLowStockItems()**

```java
.filter(item -> item.getQuantity() < 5)
```

## ✔️ 1.2 Stream API

```java
return itemDao.findAll().stream()
       .filter(item -> item.getQuantity() < 5)
       .sorted(Comparator.comparingInt(Item::getQuantity))
       .toList();
```

## ✔️ 1.3 Method References

```java
.sorted(Comparator.comparingInt(Item::getQuantity))
```

---

# 🧪 **2. Testing**

| Topic           | Where in project                        | Example            |
| --------------- | --------------------------------------- | ------------------ |
| **JUnit 4**     | `src/test/.../ItemServiceImplTest.java` | `@Test`            |
| **Mockito**     | Service layer tests                     | `@Mock ItemDao`    |
| **AAA Pattern** | whole test suite                        | Arrange–Act–Assert |

---

# 🧩 **3. Dependency Injection (CDI Concepts)**

Although CDI (`@Inject @Named @SessionScoped`) is not used directly, **Spring provides replacements**:

| CDI Concept      | Spring Equivalent             | Where Used                 |
| ---------------- | ----------------------------- | -------------------------- |
| `@Inject`        | `@Autowired`                  | Controller, Service        |
| `@Named("name")` | `@Service("fastItemService")` | ItemServiceImpl            |
| `@SessionScoped` | `@SessionScope`               | ItemServiceImpl            |

### ✔ Example used in your code:

```java
@Service("fastItemService")
public class ItemServiceImpl implements ItemService {
```

This is **CDI Named Bean** equivalent.

---

# 🛠️ **4. Build & Project Concepts**

### ✔ Maven

* `pom.xml` contains dependencies for:

  * Spring Boot
  * JOOQ
  * gRPC Client
  * JDBC
  * Lombok

### ✔ Multi-layer Project Structure

* Controller
* Service
* DAO
* Model
* DTO

---

# 🗃️ **5. Database Layer**

## ✔️ 5.1 DAO Pattern

Implemented in:

* `ItemDao`
* `ItemDaoImpl`

DAO Responsibilities:

* `save()`
* `update()`
* `findById()`
* `exists()`
* `findAll()`

## ✔️ 5.2 JOOQ Implementation

Inside **ItemDaoImpl**:

```java
dsl.insertInto(table(TABLE))
   .set(field("item_id"), item.getItemId())
   .execute();
```

## ✔️ 5.3 Try-with-Resources

Not needed because **JOOQ + Spring manages resources automatically**.

---

# 🧱 **6. Design Patterns Used**

## ✔️ 6 Factory Pattern (gRPC Client Factory)

ItemFactory with ItemService:

```
package com.demo.ecommerce.model;

public class ItemFactory {

    public static Item create(String id, String name, int qty, double price) {
        return new Item(id, name, qty, price);
    }
}

```


---

# 🔗 **7. gRPC Integration**

The `.proto` file:

```proto
service NotificationService {
  rpc NotifyInsufficientStock(NotificationRequest) returns (NotificationResponse);
}
```

Used inside `ItemServiceImpl`:

```java
notificationStub.notifyInsufficientStock(grpcRequest);
```

---

# ⚙️ **8. Core Flow Explanation**

### 1️⃣ Add Item

```
POST /api/items
```

Controller → Service → DAO → DB

### 2️⃣ Place Order

```
POST /api/orders
```

Flow:

1. Fetch item
2. Compare stock
3. If enough → update
4. If not →

   * Call **gRPC notification service**
   * Throw `InsufficientStockException`

### 3️⃣ Fetch Item

```
GET /api/items/{id}
```

---

#  **9. Clean Architecture Summary**

```
[ Controller ]
      ↓
[ Service (business logic + Strategy) ]
      ↓
[ DAO (JOOQ) ]
      ↓
[ Database ]
      ↳ (gRPC Notifier integrated from service)
```

---

# 📌 **10. Checklist of Concepts Used**

| Topic       | Used In File                          |
| ----------- | ------------------------------------- |
| Lambdas     | `ItemServiceImpl#getLowStockItems()`  |
| Streams     | `ItemServiceImpl#getLowStockItems()`  |
| DI          | All `@Autowired` / `@Service` classes |
| CDI         | `@SessionScoped` in ItemService       |
| Named Beans | `@Service("fastItemService")`         |
| DAO Pattern | `ItemDao`, `ItemDaoImpl`              |
| JOOQ        | `ItemDaoImpl`                         |
| Strategy    | Service logic + extendable            |
| Factory     |  Itemfactory                          |

---

# 🏁 **Conclusion**

JavaAeroParker is now a **showcase learning project** combining:

* Spring Boot
* DAO + JOOQ
* gRPC
* Java 8 Functional Programming
* Design Patterns
* Clean Architecture
