# My Coffee Chat

A multithreaded Java desktop application simulating a real-time coffee shop chat system between customers and baristas. Built with JavaFX for the GUI, Spring Boot for application bootstrapping, and SQLite for persistent message storage.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Design Decisions](#design-decisions)

---

## Overview

My Coffee Chat demonstrates concurrent programming patterns in a real-world context. Customers send messages and place orders through a JavaFX GUI; a pool of 10 barista threads processes incoming messages concurrently via a `LinkedBlockingQueue`, persists all conversations to a local SQLite database, and pushes responses back to the UI via a thread-safe callback mechanism.

---

## Features

- **Real-time multithreaded messaging** — 10 barista threads process orders concurrently using a producer-consumer pattern
- **Order detection** — keyword-based routing distinguishes order requests from general messages
- **Persistent chat history** — all messages stored in SQLite and reloaded on startup
- **Image uploads** — customers can attach images (JPG/PNG); previewed in a 200x200 gallery
- **Modern dark UI** — Catppuccin-inspired theme built with JavaFX CSS
- **Enter-to-send** — keyboard shortcut support in the message input field

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
| `CoffeeShop` | Manages thread pool and message queue; dispatches to barista workers |
| `Chat` | Customer-facing controller; saves message and forwards to queue |
| `DatabaseUtil` | JDBC utility for all SQLite operations (init, save, fetch) |
| `ChatApplication` | JavaFX stage/scene — renders chat, handles input, manages image gallery |
| `Message` | Data model — `id`, `sender`, `text`, `timestamp` |

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Core language |
| Spring Boot | 3.2.3 | Application framework and DI bootstrapping |
| JavaFX | 21 | Desktop GUI (controls, layouts, CSS styling) |
| SQLite (JDBC) | 3.45.1.0 | Embedded local database — zero configuration |
| Gradle | 8+ | Build tool and dependency management |
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
    │   │   ├── MyCoffeeChatApplication.java   # Entry point
    │   │   ├── model/
    │   │   │   └── Message.java               # Message data model
    │   │   ├── database/
    │   │   │   └── DatabaseUtil.java          # JDBC operations
    │   │   ├── chat/
    │   │   │   ├── Chat.java                  # Customer message controller
    │   │   │   └── CoffeeShop.java            # Barista thread manager
    │   │   └── ui/
    │   │       └── ChatApplication.java       # JavaFX GUI
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

Gradle will automatically download all declared dependencies:

- `org.springframework.boot:spring-boot-starter`
- `org.xerial:sqlite-jdbc:3.45.1.0`
- `org.openjfx:javafx-controls:21`
- `org.openjfx:javafx-fxml:21`
- `org.junit.jupiter:junit-jupiter:5.10.2`

> No database setup required. `my_coffee.db` is created automatically on first launch.

### Run

```bash
./gradlew run
```

Or open the project in IntelliJ IDEA and run `MyCoffeeChatApplication.java` directly.

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

Examples of valid order triggers:
- `order latte`
- `I want to order an espresso`
- `Can I order two flat whites?`

### Uploading an Image

Click **Upload Image**, select a JPG or PNG file, and it appears as a 200x200 thumbnail in the image gallery. The filename is also persisted to the database.

### Chat History

All messages are saved to `my_coffee.db` and automatically reloaded into the UI on every startup.

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

Both customer and barista messages are written to this table. On startup, all rows are fetched ordered by `id` to reconstruct the conversation history.

---

## Testing

### Run Unit Tests

```bash
./gradlew test
```

### Generate Coverage Report

```bash
./gradlew jacocoTestReport
```

HTML report path:

```text
build/reports/jacoco/test/html/index.html
```

### Coverage in IntelliJ IDEA

1. Open **Run** → **Edit Configurations**.
2. Select a JUnit/Gradle test configuration.
3. Run with **Run with Coverage**.
4. Open **Coverage** tool window to inspect class and line coverage.

### Coverage in Eclipse

1. Install **EclEmma** from Eclipse Marketplace.
2. Right-click project or test class.
3. Choose **Coverage As** → **JUnit Test**.
4. Review coverage highlighting in editors and the Coverage view.

### Coverage in VS Code

1. Install **Extension Pack for Java** and **Coverage Gutters**.
2. Run `./gradlew test jacocoTestReport`.
3. Open `build/reports/jacoco/test/html/index.html` for full report details.
4. Use Coverage Gutters to visualize line-level coverage from generated reports.

### Testing Best Practices Used in This Project

- Isolation via `@BeforeEach` and `@AfterEach`.
- Separate test classes by component (`Chat`, `OrderQueue`, `DatabaseUtil`, `CoffeeShop`).
- Descriptive test method naming for readability.
- Stable assertions for behavior, persistence, and singleton lifecycle checks.

---

## Design Decisions

| Decision | Rationale |
|---|---|
| `LinkedBlockingQueue` | Thread-safe producer-consumer without explicit locks or synchronization |
| `Consumer<String>` callback | Decouples barista threads from JavaFX layer — backend has no UI dependency |
| `Platform.runLater()` | Guarantees all UI mutations occur on the JavaFX Application Thread |
| 10 daemon threads | Parallel throughput; daemon flag ensures clean JVM shutdown without manual interruption |
| SQLite + JDBC | Zero-configuration embedded persistence — no external server required |
| `try-with-resources` | Guarantees JDBC connection and statement cleanup, preventing resource leaks |
| Keyword-based order detection | Lightweight routing without an NLP or external dependency |

---

## Author

**Malik Salimov** — Java Developer

<span><i>Made at <a href='https://qwasar.io'>Qwasar SV -- Software Engineering School</a></i></span>
<span><img alt='Qwasar SV -- Software Engineering School Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' /></span>
