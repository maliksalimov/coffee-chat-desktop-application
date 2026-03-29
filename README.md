# My Coffee Chat

A multithreaded Java desktop application simulating a real-time coffee shop chat system between customers and baristas. Built with JavaFX for the GUI, Spring Boot for application bootstrapping, SQLite for persistent message storage, and a custom reflection-based framework for dynamic request handling.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Framework](#framework)
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

My Coffee Chat demonstrates concurrent programming patterns in a real-world context. Customers send messages and place orders through a JavaFX GUI; a pool of 10 barista threads processes incoming messages concurrently via a `LinkedBlockingQueue`, persists all conversations to a local SQLite database, and pushes responses back to the UI via a thread-safe callback mechanism.

The application also includes a custom annotation-driven framework built on Java Reflection, allowing dynamic dispatching of client requests to handler methods without hardcoded routing logic.

---

## Features

- **Real-time multithreaded messaging** — 10 barista threads process orders concurrently using a producer-consumer pattern
- **Order detection** — keyword-based routing distinguishes order requests from general messages
- **Persistent chat history** — all messages stored in SQLite and reloaded on startup
- **Image uploads** — customers can attach images (JPG/PNG); previewed in a 200x200 gallery
- **Modern dark UI** — Catppuccin-inspired theme built with JavaFX CSS
- **Enter-to-send** — keyboard shortcut support in the message input field
- **Custom annotation framework** — reflection-based request dispatching using meta-annotations

---

## Architecture
```
Customer Input (JavaFX UI)
        │
        ▼
  Chat.sendMessage()
        │
        ├──► DatabaseUtil.saveMessage()  →  SQLite (messages table)
        │
        └──► CoffeeShop.receiveMessage()  →  LinkedBlockingQueue
                                                    │
                                    ┌───────────────┴───────────────┐
                               Barista 1 ... Barista 10  (daemon threads)
                                    │
                           processOrders() loop
                                    │
                          contains "order"?
                           ├─ YES → "Your order has been placed: ..."
                           └─ NO  → "I don't understand your order: ..."
                                    │
                           DatabaseUtil.saveMessage()
                                    │
                           onResponse callback
                                    │
                           Platform.runLater()  →  UI Update
```

### Key Components

| Component | Responsibility |
|---|---|
| `MyCoffeeChatApplication` | Entry point — initializes DB, starts barista threads, launches JavaFX |
| `CoffeeShop` | Singleton — manages thread pool and message queue; dispatches to barista workers |
| `Chat` | Customer-facing controller; validates, saves, and forwards messages |
| `DatabaseUtil` | JDBC utility for all SQLite operations (init, save, fetch) |
| `ChatApplication` | JavaFX stage/scene — renders chat, handles input, manages image gallery |
| `Message` | Data model — `id`, `sender`, `text`, `timestamp` |

---

## Framework

The `framework` package implements a lightweight annotation-driven dispatch system using Java Reflection.

### How it works

1. Methods are annotated with `@OrderHandler` or `@ChatHandler`
2. Both annotations are themselves annotated with `@RequestMappingMeta`
3. `InteractionHandler` scans all methods of a `BusinessObject` at runtime
4. Any method whose annotation is marked with `@RequestMappingMeta` is invoked automatically

No `if-else` chains. Adding a new request type requires only a new annotation and a new method.

### Framework Components

| Class | Responsibility |
|---|---|
| `BusinessObject` | Interface that all business classes implement |
| `RequestMappingMeta` | Meta-annotation marking annotations as request handlers |
| `OrderHandler` | Annotation for order-handling methods |
| `ChatHandler` | Annotation for chat-handling methods |
| `InteractionHandler` | Scans methods via reflection and dispatches requests |
| `ReflectionUtil` | Utility for direct method invocation by name |
| `CoffeeShopDemo` | Demo implementation of `BusinessObject` |
| `BusinessTestClient` | Test client demonstrating the framework |

### Example
```java
CoffeeShop coffeeShop = CoffeeShop.getInstance();
InteractionHandler handler = new InteractionHandler();

handler.handleInteraction(coffeeShop, "order", "1 Cappuccino");
handler.handleInteraction(coffeeShop, "chat", "Hello, barista!");
handler.handleInteraction(coffeeShop, "feedback", "Great service!");
```

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Core language |
| Spring Boot | 3.4.3 | Application framework and DI bootstrapping |
| JavaFX | 21 | Desktop GUI (controls, layouts, CSS styling) |
| SQLite (JDBC) | 3.45.1.0 | Embedded local database — zero configuration |
| Gradle | 9.4 | Build tool and dependency management |
| JUnit 5 | 5.10.2 | Unit testing |
| JaCoCo | bundled Gradle plugin | Test coverage reporting |

---

## Project Structure
```
my_coffee_chat/
├── build.gradle
├── settings.gradle
├── README.md
├── my_coffee.db                               # SQLite DB (auto-created at runtime)
│
└── src/
    ├── main/
    │   ├── java/com/maliksalimov/my_coffee_chat/
    │   │   ├── MyCoffeeChatApplication.java
    │   │   ├── model/
    │   │   │   └── Message.java
    │   │   ├── database/
    │   │   │   └── DatabaseUtil.java
    │   │   ├── chat/
    │   │   │   ├── Chat.java
    │   │   │   └── CoffeeShop.java
    │   │   ├── framework/
    │   │   │   ├── BusinessObject.java
    │   │   │   ├── RequestMappingMeta.java
    │   │   │   ├── OrderHandler.java
    │   │   │   ├── ChatHandler.java
    │   │   │   ├── InteractionHandler.java
    │   │   │   ├── ReflectionUtil.java
    │   │   │   ├── CoffeeShopDemo.java
    │   │   │   └── BusinessTestClient.java
    │   │   └── ui/
    │   │       └── ChatApplication.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/maliksalimov/my_coffee_chat/
            ├── ChatTest.java
            ├── OrderQueueTest.java
            ├── DatabaseUtilTest.java
            ├── CoffeeShopTest.java
            ├── MyCoffeeChatApplicationTests.java
            └── TestDatabaseSupport.java
```

---

## Getting Started

### Prerequisites

- **Java 21** (Amazon Corretto or any JDK 21+)
- **Gradle 8+** (or use the included Gradle wrapper)

### Clone
```bash
git clone https://git.us.qwasar.io/my_coffee_chat_208627_slgeta/my_coffee_chat.git
cd my_coffee_chat
```

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew run
```

---

## Usage

### Sending a Message

Type any message in the input field and press **Enter** or click **Send**.
```
You:     Hello! What do you have today?
Barista: I don't understand your order: Hello! What do you have today?
```

### Placing an Order

Include the word **"order"** anywhere in your message.
```
You:     I'd like to order a cappuccino
Barista: Your order has been placed: I'd like to order a cappuccino
```

### Uploading an Image

Click **Upload Image**, select a JPG or PNG file. The image appears as a thumbnail and the filename is saved to the database.

---

## Database Schema
```sql
CREATE TABLE IF NOT EXISTS messages (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    sender    TEXT    NOT NULL,
    text      TEXT    NOT NULL,
    timestamp TEXT    DEFAULT (datetime('now'))
);
```

---

## Testing

### Run Tests
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

| Class | What it tests |
|---|---|
| `OrderQueueTest` | Message queue processing, order detection, case sensitivity |
| `ChatTest` | Message validation, database persistence, callback invocation |
| `DatabaseUtilTest` | CRUD operations, insert order, empty state |
| `CoffeeShopTest` | Singleton lifecycle, callback replacement, thread safety |

---

## Packaging

### Build the JAR
```bash
./gradlew clean build
```

JAR is generated at:
```
build/libs/coffee-shop-app-1.0-SNAPSHOT.jar
```

> JavaFX applications require the JavaFX runtime on the module path. Use `./gradlew run` to launch the application directly.

---

## Design Decisions

| Decision | Rationale |
|---|---|
| `LinkedBlockingQueue` | Thread-safe producer-consumer without explicit locks |
| `Consumer<String>` callback | Decouples barista threads from JavaFX layer |
| `Platform.runLater()` | Guarantees UI mutations on the JavaFX Application Thread |
| 10 daemon threads | Parallel throughput; daemon flag ensures clean JVM shutdown |
| SQLite + JDBC | Zero-configuration embedded persistence |
| `try-with-resources` | Guarantees JDBC connection cleanup |
| Reflection-based dispatch | Eliminates hardcoded routing; new handlers require only a new annotation |
| Singleton `CoffeeShop` | Single thread pool shared across the application lifecycle |

---

## Author

**Malik Salimov** — Java Developer

<span><i>Made at <a href='https://qwasar.io'>Qwasar SV -- Software Engineering School</a></i></span>
<span><img alt='Qwasar SV -- Software Engineering School Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' /></span>