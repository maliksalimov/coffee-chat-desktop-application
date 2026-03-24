# Welcome to My Coffee Chat
***

## Task
The challenge was to extend an existing multithreaded coffee shop application with two major features:

1. **Chat Functionality (Part 01):** Customers needed a way to interact with baristas in real time — sending messages, placing orders, and having the entire conversation history persisted to a database using JDBC.

2. **Desktop Interface (Part 02):** The application needed a user-friendly desktop GUI built with JavaFX, allowing users to view chat history, send messages, upload images, and see barista responses — all in one window.

The core challenges included managing concurrent barista threads safely, integrating SQLite via JDBC without an ORM, and connecting a JavaFX UI to a multithreaded backend using callbacks.

## Description
The solution is built in Java 21 using Gradle, JavaFX, and SQLite (via JDBC).

**Architecture overview:**
```
MyCoffeeChatApplication   → Entry point: initializes DB, starts baristas, launches UI
├── DatabaseUtil           → JDBC utility: handles all DB connections, INSERT, SELECT
├── Message                → Model class: represents a single chat message
├── CoffeeShop             → Manages barista threads (BlockingQueue + Consumer callback)
├── Chat                   → Sends messages from the customer side
└── ChatApplication        → JavaFX UI: TextArea, TextField, Button, ListView
```

**How the chat flow works:**
```
Customer types message → Chat.sendMessage()
                              ↓
                    DatabaseUtil.saveMessage()    ← saved to SQLite
                              ↓
                    CoffeeShop.receiveMessage()   ← added to BlockingQueue
                              ↓
                    Barista Thread (processOrders()) ← picks up from queue
                              ↓
                    onResponse callback → Platform.runLater() → chatArea.appendText()
```

**Key design decisions:**
- `LinkedBlockingQueue` is used for a thread-safe message passing between the customer and barista threads
- `Consumer<String> onResponse` callback decouples the backend thread from the JavaFX UI thread
- `Platform.runLater()` ensures UI updates happen on the JavaFX Application Thread
- SQLite was chosen for zero-configuration local persistence (no server required)
- `try-with-resources` is used throughout JDBC code to prevent connection leaks

## Installation

**Prerequisites:**
- Java 21 (Amazon Corretto or any JDK 21+)
- Gradle 8+
- IntelliJ IDEA (recommended) or any Java IDE

**Clone the repository:**
```bash
git clone https://github.com/maliksalimov/my_coffee_chat.git
cd my_coffee_chat
```

**Install dependencies:**
```bash
./gradlew build
```

This will automatically download all dependencies defined in `build.gradle`:
- `org.springframework.boot:spring-boot-starter`
- `org.xerial:sqlite-jdbc:3.45.1.0`
- `org.openjfx:javafx-controls:21`
- `org.openjfx:javafx-fxml:21`
- `org.projectlombok:lombok`

> **Note:** No database setup is required. SQLite will automatically create a `my_coffee.db` file in the project root on first run.

## Usage

**Run the application:**
```bash
./gradlew run
```

Or run directly from IntelliJ IDEA by executing `MyCoffeeChatApplication.java`.

**Using the chat:**
```
# Send a regular message
> Hello, what do you have today?
Barista: I don't understand your order: Hello, what do you have today?

# Place an order (use the word "order")
> order cappuccino
Barista: Your order has been placed: order cappuccino

# Upload an image
Click "📎 Upload Image" → select a .jpg / .png / .jpeg file
The image will appear in the gallery below and be saved to the database.
```

**Order detection:**
Any message containing the word `order` (case-insensitive) is treated as an order. For example:
- `order latte` ✅
- `I want to order an espresso` ✅
- `What is on the menu?` → barista responds with a default message

**Chat history:**
All messages (both customer and barista) are saved to `my_coffee.db` and automatically loaded on startup.

### The Core Team

**Malik Salimov** — Java Developer

<span><i>Made at <a href='https://qwasar.io'>Qwasar SV -- Software Engineering School</a></i></span>
<span><img alt='Qwasar SV -- Software Engineering School\'s Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' /></span>