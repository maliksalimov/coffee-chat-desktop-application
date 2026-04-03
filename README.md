# My Coffee Chat

A multithreaded Java desktop application simulating a real-time coffee shop ordering and chat system. Built with JavaFX for the GUI, Spring Boot for dependency injection and application bootstrapping, SQLite for persistent message storage, and a custom reflection-based annotation framework for dynamic request dispatching. The project demonstrates ten software design patterns working together through a single facade entry point.

**Author:** Malik Salimov

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [Framework](#framework)
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

My Coffee Chat is a Spring Boot desktop application that combines concurrent programming, classic design patterns, and a custom annotation-driven framework. Customers interact through a JavaFX GUI; a pool of 10 daemon barista threads processes incoming messages concurrently via a `LinkedBlockingQueue`, persists all conversations to a local SQLite database, and pushes responses back to the UI through a thread-safe callback.

The application also exposes a REST API for sending and retrieving chat messages, backed by Spring Data JPA and a separate SQLite table managed by Hibernate.

---

## Features

- **Real-time multithreaded messaging** — 10 barista daemon threads process orders concurrently using a producer-consumer queue
- **Order detection** — keyword-based routing distinguishes order requests from general messages
- **Persistent chat history** — all messages stored in SQLite and reloaded on startup
- **Image uploads** — customers can attach JPG/PNG images; previewed as 200x200 thumbnails
- **Dark-themed UI** — Catppuccin-inspired JavaFX CSS styling
- **REST API** — send and retrieve messages over HTTP via Spring MVC
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
        +---> DatabaseUtil.saveMessage()  -->  SQLite (messages table)
        |
        +--->  CoffeeShop.receiveMessage()  -->  LinkedBlockingQueue
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
                               Platform.runLater()  -->  UI Update
```

### Key Components

| Component | Responsibility |
|---|---|
| `MyCoffeeChatApplication` | Entry point — initializes DB, starts barista threads, launches JavaFX |
| `CoffeeShop` | Singleton — owns the `LinkedBlockingQueue` and 10 daemon barista threads |
| `Chat` | Customer-facing controller — validates, saves, and forwards messages |
| `DatabaseUtil` | JDBC utility for all raw SQLite operations (init, save, fetch) |
| `ChatApplication` | JavaFX stage/scene — renders chat, handles input, manages image gallery |
| `CoffeeShopFacade` | Orchestrates all ten design patterns in a single `placeOrder()` call |
| `ChatService` | Spring service — delegates CRUD to `ChatRepository` via JPA |
| `ChatController` | Spring MVC REST controller — exposes `/api/chat` endpoints |
| `Message` | Legacy data model — `id`, `sender`, `text`, `timestamp` |
| `ChatMessage` | JPA entity — `id`, `sender`, `message`, `timestamp` (auto-set via `@PrePersist`) |

---

## Design Patterns

| Pattern | Package | Key Classes |
|---|---|---|
| Singleton | `chat/`, `singleton/` | `CoffeeShop` (synchronized `getInstance()`), `CoffeeShopManager` (`@Component`) |
| Factory | `factory/` | `CoffeeFactory` (`@Service`), `Coffee` (abstract), `Espresso`, `Cappuccino` |
| Observer | `observer/` | `OrderEventPublisher` (`ApplicationEventPublisher`), `OrderNotificationListener` (`@EventListener`), `OrderReadyEvent` |
| Strategy | `strategy/` | `PricingStrategy` (interface), `RegularPricingStrategy` (`@Primary`), `SilverPricingStrategy`, `GoldPricingStrategy` |
| Decorator | `decorator/` | `CoffeeDecorator`, `MilkDecorator`, `SugarDecorator`, `DecoratorService` |
| Command | `command/` | `OrderCommand` (interface), `PlaceOrderCommand`, `CancelOrderCommand`, `OrderCommandProcessor` |
| Adapter | `adapter/` | `PaymentProcessor` (interface), `ExternalPaymentService`, `PaymentAdapter`, `PaymentService` |
| Facade | `facade/` | `CoffeeShopFacade` — single `placeOrder()` entry point coordinating all patterns |
| Prototype | `prototype/` | `CoffeeOrder` (`@Scope("prototype")`), `CoffeeOrderPrototypeService` |
| Template Method | `template/` | `CoffeePreparationTemplate` (abstract), `EspressoPreparation`, `CappuccinoPreparation` |

---

## Framework

The `framework` package implements a lightweight annotation-driven dispatch system using Java Reflection.

### How It Works

1. Handler methods are annotated with `@OrderHandler` or `@ChatHandler`
2. Both annotations are themselves annotated with `@RequestMappingMeta` (meta-annotation)
3. `InteractionHandler` scans all methods of a `BusinessObject` at runtime and caches results in a `ConcurrentHashMap` to avoid repeated reflection scans
4. Any method whose annotation is marked with `@RequestMappingMeta` is invoked dynamically

No `if-else` routing chains. Adding a new request type requires only a new annotation and a new annotated method.

### Framework Components

| Class | Responsibility |
|---|---|
| `BusinessObject` | Marker interface for all dispatchable business classes |
| `RequestMappingMeta` | Meta-annotation that designates an annotation as a request handler |
| `OrderHandler` | Annotation for order-handling methods |
| `ChatHandler` | Annotation for chat-handling methods |
| `InteractionHandler` | Scans methods via reflection, caches results, and dispatches requests |
| `ReflectionUtil` | Utility for direct method invocation by name |
| `CoffeeShopDemo` | Demo `BusinessObject` implementation |
| `BusinessTestClient` | Client demonstrating the framework end-to-end |

### Example

```java
CoffeeShop coffeeShop = CoffeeShop.getInstance();
InteractionHandler handler = new InteractionHandler();

handler.handleInteraction(coffeeShop, "order", "1 Cappuccino");
handler.handleInteraction(coffeeShop, "chat", "Hello, barista!");
```

---

## REST API

All endpoints are served by `ChatController` and backed by `ChatService` / `ChatRepository` (Spring Data JPA, `chat_messages` table).

| Method | Endpoint | Request Body | Description |
|---|---|---|---|
| `POST` | `/api/chat/send` | `{ "sender": "Alice", "message": "Hello" }` | Save a new chat message |
| `GET` | `/api/chat/messages` | — | Retrieve all messages |
| `GET` | `/api/chat/messages/{sender}` | — | Retrieve all messages by a specific sender |

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Core language |
| Spring Boot | 3.4.3 | Application framework, DI, auto-configuration |
| JavaFX | 21 | Desktop GUI — controls, layouts, CSS styling |
| Spring Data JPA | (Spring Boot managed) | Repository abstraction over Hibernate |
| Hibernate Community SQLite Dialect | 6.0 | Hibernate dialect for SQLite |
| SQLite JDBC | 3.45.1.0 | Embedded database driver |
| Gradle | 9.4 | Build tool and dependency management |
| JUnit 5 | (Spring Boot managed) | Unit and integration testing |
| Mockito | (Spring Boot managed) | Mocking framework for unit tests |
| JaCoCo | (Gradle plugin) | Test coverage reporting |

---

## Project Structure

```
my_coffee_chat/
+-- build.gradle
+-- settings.gradle
+-- README.md
+-- my_coffee.db                                  # SQLite DB (auto-created at runtime)
|
+-- src/
    +-- main/
    |   +-- java/com/maliksalimov/my_coffee_chat/
    |   |   +-- MyCoffeeChatApplication.java
    |   |   +-- chat/
    |   |   |   +-- Chat.java
    |   |   |   +-- CoffeeShop.java               # Singleton, LinkedBlockingQueue, 10 barista threads
    |   |   +-- chat2/
    |   |   |   +-- ChatMessage.java              # JPA entity
    |   |   |   +-- ChatRepository.java           # JpaRepository
    |   |   |   +-- ChatService.java
    |   |   |   +-- ChatController.java           # REST endpoints
    |   |   +-- database/
    |   |   |   +-- DatabaseUtil.java             # Raw JDBC
    |   |   +-- model/
    |   |   |   +-- Message.java
    |   |   +-- ui/
    |   |   |   +-- ChatApplication.java          # JavaFX stage
    |   |   +-- framework/
    |   |   |   +-- BusinessObject.java
    |   |   |   +-- RequestMappingMeta.java
    |   |   |   +-- OrderHandler.java
    |   |   |   +-- ChatHandler.java
    |   |   |   +-- InteractionHandler.java       # ConcurrentHashMap method cache
    |   |   |   +-- ReflectionUtil.java
    |   |   |   +-- CoffeeShopDemo.java
    |   |   |   +-- BusinessTestClient.java
    |   |   +-- singleton/
    |   |   |   +-- CoffeeShopManager.java
    |   |   +-- factory/
    |   |   |   +-- Coffee.java
    |   |   |   +-- Espresso.java
    |   |   |   +-- Cappuccino.java
    |   |   |   +-- CoffeeFactory.java
    |   |   +-- observer/
    |   |   |   +-- OrderReadyEvent.java
    |   |   |   +-- OrderNotificationListener.java
    |   |   |   +-- OrderEventPublisher.java
    |   |   +-- strategy/
    |   |   |   +-- PricingStrategy.java
    |   |   |   +-- RegularPricingStrategy.java   # @Primary
    |   |   |   +-- SilverPricingStrategy.java
    |   |   |   +-- GoldPricingStrategy.java
    |   |   +-- decorator/
    |   |   |   +-- CoffeeDecorator.java
    |   |   |   +-- MilkDecorator.java
    |   |   |   +-- SugarDecorator.java
    |   |   |   +-- DecoratorService.java
    |   |   +-- command/
    |   |   |   +-- OrderCommand.java
    |   |   |   +-- PlaceOrderCommand.java
    |   |   |   +-- CancelOrderCommand.java
    |   |   |   +-- OrderCommandProcessor.java
    |   |   +-- adapter/
    |   |   |   +-- PaymentProcessor.java
    |   |   |   +-- ExternalPaymentService.java
    |   |   |   +-- PaymentAdapter.java
    |   |   |   +-- PaymentService.java
    |   |   +-- facade/
    |   |   |   +-- CoffeeShopFacade.java
    |   |   +-- prototype/
    |   |   |   +-- CoffeeOrder.java              # @Scope("prototype")
    |   |   |   +-- CoffeeOrderPrototypeService.java
    |   |   +-- template/
    |   |   |   +-- CoffeePreparationTemplate.java
    |   |   |   +-- EspressoPreparation.java
    |   |   |   +-- CappuccinoPreparation.java
    |   |   +-- multithreading/
    |   |       +-- Order.java
    |   |       +-- OrderQueue.java               # LinkedBlockingQueue wrapper
    |   |       +-- BaristaService.java           # @Async
    |   |       +-- CustomerService.java
    |   +-- resources/
    |       +-- application.properties
    +-- test/
        +-- java/com/maliksalimov/my_coffee_chat/
            +-- ChatTest.java
            +-- CoffeeShopTest.java
            +-- CoffeeShopSingletonTest.java
            +-- DatabaseUtilTest.java
            +-- OrderQueueTest.java
            +-- OrderQueueUnitTest.java
            +-- ChatServiceTest.java
            +-- TestDatabaseSupport.java
```

---

## Getting Started

### Prerequisites

- Java 21 (Amazon Corretto or any JDK 21+)
- Gradle 9.4+ (or use the included Gradle wrapper — no separate installation required)

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

> JavaFX requires the JavaFX runtime on the module path. Use `./gradlew run` to launch the application. Do not use `java -jar` directly.

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

The application uses two SQLite tables.

### Legacy table — `messages` (raw JDBC, `DatabaseUtil`)

```sql
CREATE TABLE IF NOT EXISTS messages (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    sender    TEXT    NOT NULL,
    text      TEXT    NOT NULL,
    timestamp TEXT    DEFAULT (datetime('now'))
);
```

### JPA table — `chat_messages` (Hibernate, `ChatRepository`)

```sql
CREATE TABLE IF NOT EXISTS chat_messages (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    sender    TEXT,
    message   TEXT,
    timestamp TEXT
);
```

The `timestamp` column is populated automatically via the `@PrePersist` lifecycle hook on `ChatMessage`.

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

| Class | What It Tests |
|---|---|
| `OrderQueueTest` | `CoffeeShop` message processing — order detection, case sensitivity, concurrent messages, DB persistence |
| `OrderQueueUnitTest` | `OrderQueue` directly — `addOrder` size increase, `takeOrder` correctness, concurrent thread safety |
| `ChatTest` | `Chat` — message validation, database persistence, callback invocation |
| `DatabaseUtilTest` | `DatabaseUtil` — CRUD operations, insert ordering, empty initial state |
| `CoffeeShopTest` | `CoffeeShop` — singleton lifecycle, callback replacement, idempotent `startBaristas` |
| `CoffeeShopSingletonTest` | `CoffeeShop` — singleton identity, safe multiple `startBaristas` calls, `null` message throws `NullPointerException` |
| `ChatServiceTest` | `ChatService` — Mockito unit tests verifying `save`, `findAll`, and `findBySender` delegation |
| `TestDatabaseSupport` | Shared test utility — initializes and clears the SQLite `messages` table between tests |

All unit tests use plain JUnit 5 and Mockito. No `@SpringBootTest` or Spring context is loaded in unit test classes.

---

## Packaging

### Build the JAR

```bash
./gradlew clean build
```

The JAR is generated at:

```
build/libs/coffee-shop-app-1.0-SNAPSHOT.jar
```

> JavaFX applications require the JavaFX runtime modules on the module path and cannot be launched with `java -jar` alone. Use `./gradlew run` for local execution.

---

## Design Decisions

| Decision | Rationale |
|---|---|
| `LinkedBlockingQueue` for message passing | Thread-safe producer-consumer without explicit lock management; blocks producers only when the queue is full |
| `@Async` on `BaristaService` | Non-blocking order processing — callers return immediately while processing continues on a Spring-managed thread pool |
| `ConcurrentHashMap` cache in `InteractionHandler` | Reflection scanning is performed once per class and cached; avoids repeated method traversal on every request dispatch |
| `@Primary` on `RegularPricingStrategy` | Resolves Spring's ambiguity when multiple `PricingStrategy` beans exist; no qualifier annotation needed at injection sites |
| Constructor injection throughout | Explicit dependencies; enables immutable fields and straightforward unit testing without a Spring context |
| `@Scope("prototype")` on `CoffeeOrder` | Ensures each `getBean` call returns a new instance; prevents shared mutable state across orders |
| `ApplicationEventPublisher` for the Observer pattern | Fully decoupled publisher and listener — the publisher has no compile-time dependency on any listener |
| `CoffeeShopFacade.placeOrder()` | Single entry point that coordinates all ten patterns; callers are isolated from internal pattern interactions |
| `@PrePersist` for timestamp | Timestamp is set by the JPA lifecycle hook rather than by application code, keeping `ChatMessage` consistent regardless of how it is saved |
| Daemon barista threads | JVM can shut down cleanly without waiting for barista threads to finish processing; threads block on `queue.take()` when idle |
| SQLite with no server | Zero-configuration embedded persistence appropriate for a desktop application; the database file is created automatically on first run |

---

## Author

**Malik Salimov**

<span><i>Made at <a href='https://qwasar.io'>Qwasar SV -- Software Engineering School</a></i></span>
<span><img alt='Qwasar SV -- Software Engineering School Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' /></span>
