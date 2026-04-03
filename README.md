# My Coffee Chat

A multithreaded Java desktop application simulating a real-time coffee shop ordering and chat system. Built with JavaFX for the GUI, Spring Boot for dependency injection and application bootstrapping, SQLite for persistent message storage, and a custom reflection-based annotation framework for dynamic request dispatching. The project demonstrates ten classic software design patterns working together through a single facade entry point.

**Author:** Malik Salimov

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [Custom Framework](#custom-framework)
- [REST API](#rest-api)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Packaging](#packaging)
- [Design Decisions](#design-decisions)

---

## Overview

My Coffee Chat is a Spring Boot desktop application that combines concurrent programming, classic design patterns, and a custom annotation-driven framework. Customers interact through a JavaFX GUI. A pool of 10 daemon barista threads processes incoming messages concurrently via a `LinkedBlockingQueue`, persists all conversations to a local SQLite database, and pushes responses back to the UI through a thread-safe callback.

The application also exposes a REST API for sending and retrieving chat messages, backed by Spring Data JPA and a dedicated `chat_messages` table managed by Hibernate.

The project is structured across four incremental development phases:

| Phase | Focus |
|---|---|
| Part 01 | Core multithreaded chat engine with JavaFX GUI and raw JDBC persistence |
| Part 02 | Spring Async (`@Async`), `OrderQueue`, `BaristaService`, `CustomerService` |
| Part 03 | Spring Data JPA, Hibernate, ten design patterns, REST API |
| Part 04 | Comprehensive test suite — unit, integration, and JaCoCo coverage |

---

## Features

- **Real-time multithreaded messaging** — 10 barista daemon threads process orders concurrently via a producer-consumer queue
- **Order detection** — keyword-based routing distinguishes order requests from general messages
- **Persistent chat history** — all messages stored in SQLite and reloaded on startup
- **Image uploads** — customers can attach JPG/PNG images, previewed as 200x200 thumbnails
- **Dark-themed desktop UI** — Catppuccin-inspired JavaFX CSS styling
- **REST API** — send and retrieve chat messages over HTTP via Spring MVC
- **Ten design patterns** — Factory, Singleton, Observer, Strategy, Decorator, Command, Adapter, Facade, Prototype, and Template Method, all coordinated through `CoffeeShopFacade`
- **Custom annotation framework** — reflection-based request dispatching with a `ConcurrentHashMap` method cache; no hardcoded routing logic

---

## Architecture

```
Customer Input (JavaFX UI)
        |
        v
  Chat.sendMessage()
        |
        +---> DatabaseUtil.saveMessage()  -------> SQLite (messages table)
        |
        +---> CoffeeShop.receiveMessage() -------> LinkedBlockingQueue
                                                          |
                                          +--------------+---------------+
                                     Barista 1  ...  Barista 10  (daemon threads)
                                          |
                                 processOrders() loop
                                          |
                                contains "order"?
                                 +- YES --> "Your order has been placed: ..."
                                 +- NO  --> "I don't understand your order: ..."
                                          |
                                 DatabaseUtil.saveMessage()
                                          |
                                 onResponse callback
                                          |
                                 Platform.runLater() -------> UI update

REST Client
        |
        v
  POST /api/chat/send
  GET  /api/chat/messages
  GET  /api/chat/messages/{sender}
        |
        v
  ChatController --> ChatService --> ChatRepository (JPA) --> SQLite (chat_messages table)
```

### Key Components

| Component | Responsibility |
|---|---|
| `MyCoffeeChatApplication` | Entry point — initializes the database, starts barista threads, and launches JavaFX |
| `CoffeeShop` | Singleton — owns the `LinkedBlockingQueue` and manages 10 daemon barista threads |
| `Chat` | Customer-facing controller — validates, saves, and forwards messages to the queue |
| `DatabaseUtil` | Raw JDBC utility for all SQLite operations (schema init, insert, fetch) |
| `ChatApplication` | JavaFX stage and scene — renders the chat window, handles input, and manages the image gallery |
| `CoffeeShopFacade` | Orchestrates all ten design patterns through a single `placeOrder()` call |
| `ChatController` | Spring MVC REST controller — exposes the `/api/chat` endpoints |
| `ChatService` | Spring service — delegates CRUD operations to `ChatRepository` |
| `ChatRepository` | Spring Data JPA repository — provides `findAll`, `save`, and `findBySender` |
| `Message` | Legacy JDBC data model — `id`, `sender`, `text`, `timestamp` |
| `ChatMessage` | JPA entity — `id`, `sender`, `message`, `timestamp` (auto-set via `@PrePersist`) |

---

## Design Patterns

| Pattern | Package | Key Classes | Notes |
|---|---|---|---|
| Singleton | `chat/`, `singleton/` | `CoffeeShop`, `CoffeeShopManager` | `CoffeeShop` uses synchronized `getInstance()`; `CoffeeShopManager` is a Spring `@Component` |
| Factory | `factory/` | `CoffeeFactory`, `Coffee`, `Espresso`, `Cappuccino` | `CoffeeFactory` is a `@Service`; `Coffee` is the abstract product |
| Observer | `observer/` | `OrderEventPublisher`, `OrderNotificationListener`, `OrderReadyEvent` | Uses Spring's `ApplicationEventPublisher` and `@EventListener` for full decoupling |
| Strategy | `strategy/` | `PricingStrategy`, `RegularPricingStrategy`, `SilverPricingStrategy`, `GoldPricingStrategy` | `RegularPricingStrategy` is `@Primary` to resolve Spring autowiring ambiguity |
| Decorator | `decorator/` | `CoffeeDecorator`, `MilkDecorator`, `SugarDecorator`, `DecoratorService` | Wraps `Coffee` instances at runtime to add cost and description |
| Command | `command/` | `OrderCommand`, `PlaceOrderCommand`, `CancelOrderCommand`, `OrderCommandProcessor` | `OrderCommandProcessor` queues and executes command objects |
| Adapter | `adapter/` | `PaymentProcessor`, `ExternalPaymentService`, `PaymentAdapter`, `PaymentService` | `PaymentAdapter` bridges the internal `PaymentProcessor` interface to `ExternalPaymentService` |
| Facade | `facade/` | `CoffeeShopFacade` | Single `placeOrder()` entry point that coordinates all ten patterns in sequence |
| Prototype | `prototype/` | `CoffeeOrder`, `CoffeeOrderPrototypeService` | `CoffeeOrder` is `@Scope("prototype")` — each `getBean` returns a fresh instance |
| Template Method | `template/` | `CoffeePreparationTemplate`, `EspressoPreparation`, `CappuccinoPreparation` | Abstract class defines the preparation steps; subclasses provide the concrete implementation |

---

## Custom Framework

The `framework` package implements a lightweight annotation-driven dispatch system using Java Reflection.

### How It Works

1. Handler methods are annotated with `@OrderHandler` or `@ChatHandler`
2. Both annotations are meta-annotated with `@RequestMappingMeta`
3. `InteractionHandler` scans all methods of a `BusinessObject` at startup and caches the results in a `ConcurrentHashMap` — reflection occurs once per class, not once per request
4. On each invocation, the cached method list is consulted and any method whose annotation carries `@RequestMappingMeta` is called dynamically via `ReflectionUtil`

Adding a new request type requires only a new annotation (itself annotated with `@RequestMappingMeta`) and a new annotated method — no routing logic to modify.

### Framework Components

| Class | Responsibility |
|---|---|
| `BusinessObject` | Marker interface that all dispatchable business classes implement |
| `RequestMappingMeta` | Meta-annotation that designates an annotation as a request handler |
| `OrderHandler` | Annotation marking a method as an order handler |
| `ChatHandler` | Annotation marking a method as a chat handler |
| `InteractionHandler` | Scans methods via reflection, caches by class, and dispatches requests |
| `ReflectionUtil` | Utility for invoking methods by name using reflection |
| `CoffeeShopDemo` | Reference implementation of `BusinessObject` |
| `BusinessTestClient` | Client that demonstrates the framework end-to-end |

### Example

```java
CoffeeShop coffeeShop = CoffeeShop.getInstance();
InteractionHandler handler = new InteractionHandler();

handler.handleInteraction(coffeeShop, "order", "1 Cappuccino");
handler.handleInteraction(coffeeShop, "chat", "Hello, barista!");
```

---

## REST API

All endpoints are served by `ChatController` under the `/api/chat` base path. The service layer is backed by Spring Data JPA and the `chat_messages` SQLite table.

### Endpoints

| Method | Endpoint | Request Body | Response | Description |
|---|---|---|---|---|
| `POST` | `/api/chat/send` | `{ "sender": "Alice", "message": "Hello" }` | `ChatMessage` JSON | Persists and returns the saved message |
| `GET` | `/api/chat/messages` | — | `ChatMessage[]` JSON | Returns all stored messages |
| `GET` | `/api/chat/messages/{sender}` | — | `ChatMessage[]` JSON | Returns all messages from the given sender |

### Response Schema

```json
{
  "id": 1,
  "sender": "Alice",
  "message": "I would like to order a latte",
  "timestamp": "2026-04-03T12:00:00"
}
```

### Example

```bash
curl -X POST http://localhost:8080/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{"sender": "Alice", "message": "order latte"}'

curl http://localhost:8080/api/chat/messages
curl http://localhost:8080/api/chat/messages/Alice
```

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Core language (source and target compatibility) |
| Spring Boot | 3.4.3 | Application framework — DI, auto-configuration, embedded Tomcat |
| JavaFX | 21 | Desktop GUI — controls, layouts, CSS styling |
| Spring Data JPA | (Spring Boot managed) | Repository abstraction over Hibernate ORM |
| Hibernate Community SQLite Dialect | 6.4.4 | Hibernate dialect for SQLite |
| SQLite JDBC | 3.45.1.0 | Embedded database driver |
| Gradle | 9.4 | Build tool and dependency management |
| JUnit 5 | (Spring Boot managed) | Unit and integration testing |
| Mockito | (Spring Boot managed) | Mock and stub framework for unit tests |
| JaCoCo | (Gradle plugin) | Test coverage reporting |

---

## Project Structure

```
my_coffee_chat/
+-- build.gradle
+-- settings.gradle
+-- README.md
+-- my_coffee.db                                     # SQLite database (auto-created on first run)
|
+-- src/
    +-- main/
    |   +-- java/com/maliksalimov/my_coffee_chat/
    |   |   +-- MyCoffeeChatApplication.java          # @SpringBootApplication entry point
    |   |   +-- chat/
    |   |   |   +-- Chat.java
    |   |   |   +-- CoffeeShop.java                   # Singleton + LinkedBlockingQueue + 10 barista threads
    |   |   +-- chat2/
    |   |   |   +-- ChatMessage.java                  # @Entity mapped to chat_messages
    |   |   |   +-- ChatRepository.java               # JpaRepository<ChatMessage, Long>
    |   |   |   +-- ChatService.java                  # @Service
    |   |   |   +-- ChatController.java               # @RestController /api/chat
    |   |   +-- database/
    |   |   |   +-- DatabaseUtil.java                 # Raw JDBC — init, save, fetch
    |   |   +-- model/
    |   |   |   +-- Message.java                      # Legacy JDBC model
    |   |   +-- ui/
    |   |   |   +-- ChatApplication.java              # JavaFX Application subclass
    |   |   +-- framework/
    |   |   |   +-- BusinessObject.java               # Marker interface
    |   |   |   +-- RequestMappingMeta.java           # Meta-annotation
    |   |   |   +-- OrderHandler.java                 # Handler annotation
    |   |   |   +-- ChatHandler.java                  # Handler annotation
    |   |   |   +-- InteractionHandler.java           # Reflection dispatcher + ConcurrentHashMap cache
    |   |   |   +-- ReflectionUtil.java
    |   |   |   +-- CoffeeShopDemo.java
    |   |   |   +-- BusinessTestClient.java
    |   |   +-- singleton/
    |   |   |   +-- CoffeeShopManager.java            # @Component singleton
    |   |   +-- factory/
    |   |   |   +-- Coffee.java                       # Abstract product
    |   |   |   +-- Espresso.java
    |   |   |   +-- Cappuccino.java
    |   |   |   +-- CoffeeFactory.java                # @Service factory
    |   |   +-- observer/
    |   |   |   +-- OrderReadyEvent.java
    |   |   |   +-- OrderNotificationListener.java    # @EventListener
    |   |   |   +-- OrderEventPublisher.java          # ApplicationEventPublisher wrapper
    |   |   +-- strategy/
    |   |   |   +-- PricingStrategy.java              # Interface
    |   |   |   +-- RegularPricingStrategy.java       # @Primary
    |   |   |   +-- SilverPricingStrategy.java
    |   |   |   +-- GoldPricingStrategy.java
    |   |   +-- decorator/
    |   |   |   +-- CoffeeDecorator.java              # Abstract decorator
    |   |   |   +-- MilkDecorator.java
    |   |   |   +-- SugarDecorator.java
    |   |   |   +-- DecoratorService.java
    |   |   +-- command/
    |   |   |   +-- OrderCommand.java                 # Command interface
    |   |   |   +-- PlaceOrderCommand.java
    |   |   |   +-- CancelOrderCommand.java
    |   |   |   +-- OrderCommandProcessor.java
    |   |   +-- adapter/
    |   |   |   +-- PaymentProcessor.java             # Target interface
    |   |   |   +-- ExternalPaymentService.java       # Adaptee
    |   |   |   +-- PaymentAdapter.java               # Adapter
    |   |   |   +-- PaymentService.java
    |   |   +-- facade/
    |   |   |   +-- CoffeeShopFacade.java             # Coordinates all 10 patterns
    |   |   +-- prototype/
    |   |   |   +-- CoffeeOrder.java                  # @Scope("prototype")
    |   |   |   +-- CoffeeOrderPrototypeService.java
    |   |   +-- template/
    |   |   |   +-- CoffeePreparationTemplate.java    # Abstract template
    |   |   |   +-- EspressoPreparation.java
    |   |   |   +-- CappuccinoPreparation.java
    |   |   +-- multithreading/
    |   |       +-- Order.java
    |   |       +-- OrderQueue.java                   # LinkedBlockingQueue wrapper
    |   |       +-- BaristaService.java               # @Async order processor
    |   |       +-- CustomerService.java
    |   +-- resources/
    |       +-- application.properties
    +-- test/
        +-- java/com/maliksalimov/my_coffee_chat/
        |   +-- ChatTest.java
        |   +-- CoffeeShopTest.java
        |   +-- CoffeeShopSingletonTest.java
        |   +-- DatabaseUtilTest.java
        |   +-- OrderQueueTest.java
        |   +-- OrderQueueUnitTest.java
        |   +-- ChatServiceTest.java
        |   +-- ChatControllerIntegrationTest.java    # @SpringBootTest + @MockitoBean
        |   +-- TestDatabaseSupport.java
        +-- resources/
            +-- mockito-extensions/
                +-- org.mockito.plugins.MockMaker     # Forces subclass mock maker for Java 21+ compatibility
```

---

## Getting Started

### Prerequisites

- Java 21+ (Amazon Corretto, Eclipse Temurin, or any compatible JDK)
- No separate Gradle installation required — use the included `gradlew` wrapper

### Clone

```bash
git clone https://github.com/maliksalimov/coffee-chat-desktop-application.git
cd coffee-chat-desktop-application
```

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew run
```

> **Note:** JavaFX requires its runtime modules on the module path. Always use `./gradlew run` to launch the application. Running the JAR directly with `java -jar` will fail without additional module path configuration.

---

## Usage

### Sending a Message

Type any message in the input field and press **Enter** or click **Send**.

```
You:     Hello! What do you have today?
Barista: I don't understand your order: Hello! What do you have today?
```

### Placing an Order

Include the word **"order"** (case-insensitive) anywhere in your message.

```
You:     I'd like to order a cappuccino
Barista: Your order has been placed: I'd like to order a cappuccino
```

### Uploading an Image

Click **Upload Image** and select a JPG or PNG file. The image appears as a 200x200 thumbnail in the chat window, and the filename is persisted to the database.

---

## Database Schema

The application maintains two independent SQLite tables. Both are stored in `my_coffee.db`, which is auto-created on the first run.

### `messages` — legacy table (raw JDBC via `DatabaseUtil`)

```sql
CREATE TABLE IF NOT EXISTS messages (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    sender    TEXT    NOT NULL,
    text      TEXT    NOT NULL,
    timestamp TEXT    DEFAULT (datetime('now'))
);
```

### `chat_messages` — JPA table (Hibernate via `ChatRepository`)

```sql
CREATE TABLE IF NOT EXISTS chat_messages (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    sender    TEXT,
    message   TEXT,
    timestamp TEXT
);
```

The `timestamp` column in `chat_messages` is populated automatically by the `@PrePersist` lifecycle callback on `ChatMessage`, ensuring consistent timestamps regardless of how the entity is created.

---

## Testing

### Run All Tests

```bash
./gradlew test
```

### Generate Coverage Report

```bash
./gradlew jacocoTestReport
```

Reports are available at:

- Test results: `build/reports/tests/test/index.html`
- Coverage report: `build/reports/jacoco/test/html/index.html`

### Test Classes

| Class | Type | What It Tests |
|---|---|---|
| `ChatTest` | Unit | `Chat` — message validation, database persistence, callback invocation |
| `DatabaseUtilTest` | Unit | `DatabaseUtil` — schema creation, CRUD operations, insert ordering, empty initial state |
| `OrderQueueTest` | Unit | `CoffeeShop` message pipeline — order detection, case sensitivity, concurrent processing, DB persistence |
| `OrderQueueUnitTest` | Unit | `OrderQueue` directly — `addOrder` increases size, `takeOrder` returns the correct item, thread safety under 20 concurrent writers |
| `CoffeeShopTest` | Unit | `CoffeeShop` — singleton lifecycle, callback replacement, idempotent `startBaristas` |
| `CoffeeShopSingletonTest` | Unit | `CoffeeShop` — singleton identity via `assertSame`, null message throws `NullPointerException` |
| `ChatServiceTest` | Unit | `ChatService` — Mockito mocks verify `save`, `findAll`, and `findBySender` are called exactly once with the correct arguments |
| `ChatControllerIntegrationTest` | Integration | `ChatController` — all three REST endpoints via `MockMvc`; `@MockitoBean` stubs `ChatService` so no database is touched |
| `TestDatabaseSupport` | Utility | Shared helper — initializes and clears the SQLite `messages` table between tests |

### Notes

- All unit tests use plain JUnit 5 with `@ExtendWith(MockitoExtension.class)`. No Spring context is loaded.
- `ChatControllerIntegrationTest` uses `@SpringBootTest(webEnvironment = RANDOM_PORT)` with `@AutoConfigureMockMvc` and `@MockitoBean` (`@MockitoBean` is the Spring Boot 3.4.x replacement for the deprecated `@MockBean`).
- `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` configures Mockito to use the **subclass mock maker** instead of the default inline mock maker. This is required for compatibility with Java 21+ where the inline mock maker's dynamic agent loading is restricted by the JVM.

---

## Packaging

### Build the JAR

```bash
./gradlew clean build
```

The executable JAR is generated at:

```
build/libs/coffee-shop-app-1.0-SNAPSHOT.jar
```

> JavaFX applications require the JavaFX runtime modules on the module path. The JAR cannot be run with `java -jar` alone on standard JDKs. Use `./gradlew run` for local execution.

---

## Design Decisions

| Decision | Rationale |
|---|---|
| `LinkedBlockingQueue` for message passing | Thread-safe producer-consumer without explicit lock management; blocks producers naturally when the queue is at capacity |
| `@Async` on `BaristaService` | Non-blocking order processing — the calling thread returns immediately while work continues on a Spring-managed thread pool |
| `ConcurrentHashMap` cache in `InteractionHandler` | Reflection scanning is performed once per class on first access and cached; avoids re-scanning method arrays on every dispatch call |
| `@Primary` on `RegularPricingStrategy` | Resolves Spring's ambiguity when multiple `PricingStrategy` beans are present; injection sites require no `@Qualifier` |
| Constructor injection throughout | Makes all dependencies explicit and final; enables straightforward unit testing without a Spring context |
| `@Scope("prototype")` on `CoffeeOrder` | Each `getBean` call returns a new, independent instance; prevents shared mutable state between concurrent orders |
| `ApplicationEventPublisher` for Observer | Publisher has no compile-time dependency on any listener; adding or removing listeners requires no changes to the publisher |
| `CoffeeShopFacade.placeOrder()` | Single entry point that drives all ten patterns in a defined sequence; client code is completely isolated from internal complexity |
| `@PrePersist` for timestamp on `ChatMessage` | Timestamp assignment is a persistence concern, not a business concern; the entity manages its own lifecycle hook |
| Daemon barista threads | JVM exits cleanly without waiting for blocked `queue.take()` calls; no explicit shutdown coordination needed |
| Two separate SQLite tables | `messages` serves the legacy JDBC layer (Part 01) without modification; `chat_messages` serves the JPA layer (Part 03) independently, preserving backward compatibility across phases |
| `mock-maker-subclass` in test resources | Mockito 5.x defaults to the inline mock maker, which requires unrestricted dynamic agent loading. Java 21+ restricts this by default. The subclass mock maker achieves the same mocking behavior without needing native access flags or `--add-opens` JVM arguments |

---

## Author

**Malik Salimov**

<span><i>Made at <a href='https://qwasar.io'>Qwasar SV -- Software Engineering School</a></i></span>
<span><img alt='Qwasar SV -- Software Engineering School Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' /></span>
