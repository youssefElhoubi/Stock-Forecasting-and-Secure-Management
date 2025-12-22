# Intelligent Multi-Warehouse Stock Management System (IMS)

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green) ![Docker](https://img.shields.io/badge/Docker-Enabled-blue) ![Build](https://img.shields.io/badge/Build-Passing-brightgreen)

## 📋 Project Overview

This project is a secure, intelligent backend solution designed for a distribution company managing multiple warehouses. It addresses two critical business problems:

1.  **Inefficient Stock Management:** Uses an **AI-driven forecasting module** to predict stockouts and recommend order quantities based on sales history.
2.  **Data Security:** Implements strict **Role-Based Access Control (RBAC)** and **database encryption** to protect sensitive financial data (purchase prices and margins).

---

## 🚀 Key Features

### 🔐 Security & Access Control
* **JWT Authentication:** Secure stateless authentication mechanism.
* **Role Management:**
    * **ADMIN:** Full system access. View all warehouses, manage products, view encrypted financial data (margins/buy prices), manage users.
    * **MANAGER:** Restricted to a single assigned warehouse. Can only manage local stock and view local history. Cannot see sensitive pricing data or other warehouses.
* **Data Encryption:** Sensitive fields (Purchase Price, Margin) are encrypted in the database using AES (via JPA Attribute Converters).

### 🧠 AI Forecasting Module
* Analyzes `SalesHistory` to predict future demand.
* Generates a 30-day forecast based on moving averages.
* Provides actionable recommendations (e.g., "Order 200 units", "Stock Sufficient").
* **Automation:** Predictions are updated automatically via a nightly Spring Scheduled Job.

### 🏭 Warehouse & Stock Management
* Multi-warehouse architecture.
* Real-time stock tracking with alert thresholds.
* Sales history recording and visualization.

---

## 🛠️ Technical Stack

* **Language:** Java 17+
* **Framework:** Spring Boot 3.x
    * Spring Web
    * Spring Data JPA
    * Spring Security
    * Spring Validation
* **Database:** PostgreSQL (Production) / H2 (Dev/Test)
* **Tools:** Lombok, MapStruct (or ModelMapper)
* **Testing:** JUnit 5, Mockito
* **DevOps:** Docker, Docker Compose, GitHub Actions (CI/CD)

---

## 🏗️ Architecture

The application follows a strict **Layered Architecture**:

1.  **Controller Layer:** REST API endpoints handling HTTP requests.
2.  **Service Layer:** Business logic, security validations, and AI calculations.
3.  **Repository Layer:** Data access using Spring Data JPA.
4.  **DTOs & Mappers:** Decouples the internal entities from the API response to ensure data privacy.
5.  **Exception Handling:** Centralized `@ControllerAdvice` for clean, user-friendly error messages.

---

## 💻 Getting Started

### Prerequisites
* Docker & Docker Compose
* Java 17 SDK (if running locally without Docker)
* Maven

### Option 1: Run with Docker (Recommended)
This will set up the Application and the Database automatically.

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/your-username/stock-management-system.git](https://github.com/your-username/stock-management-system.git)
    cd stock-management-system
    ```

2.  **Build and Run**
    ```bash
    docker-compose up --build
    ```

3.  **Access the Application**
    * API Base URL: `http://localhost:8080/api`
    * Database Console (if H2 enabled): `http://localhost:8080/h2-console`

### Option 2: Run Locally (Dev Mode)

1.  **Configure Database**
    Ensure `src/main/resources/application-dev.yml` is configured for H2 or your local Postgres instance.

2.  **Run with Maven**
    ```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    ```

---

## 🧪 Testing the API

### Default Credentials (Seeded Data)
* **Admin User:**
    * Username: `admin`
    * Password: `password123`
* **Manager User:**
    * Username: `manager_paris`
    * Password: `password123`

### Key Endpoints

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Login to get JWT Token | Public |
| `GET` | `/api/products` | List all products | Authenticated |
| `POST` | `/api/products` | Create a product | Admin |
| `GET` | `/api/warehouses` | List warehouses | Admin |
| `PUT` | `/api/stocks/{productId}` | Update stock quantity | Manager |
| `GET` | `/api/forecasts` | View AI predictions | Admin/Manager |

> **Note:** For Manager requests, the system automatically filters data based on the warehouse assigned to the logged-in user in the JWT token.

---

## 🤖 AI Algorithm Logic

The forecasting system currently uses a **Simple Moving Average (SMA)** approach combined with threshold analysis:

1.  **Data Ingestion:** Retrieves sales history for the last 3 months.
2.  **Calculation:**
    * `Daily Average` = (Sum of Quantity Sold) / (Number of Days)
    * `Forecast (30 days)` = `Daily Average` * 30
3.  **Recommendation Engine:**
    * If `Stock` < `Forecast`: **"Alert: Order [Diff] units"**
    * If `Stock` > `Forecast` * 1.5: **"Warning: Overstock"**
    * Else: **"Stock Sufficient"**

---

## 📦 CI/CD Pipeline

The project uses **GitHub Actions** for Continuous Integration.
* **Trigger:** Push to `main` branch.
* **Steps:**
    1.  Checkout code.
    2.  Set up JDK 17.
    3.  Run Unit Tests (`mvn test`).
    4.  Build JAR (`mvn package`).

---

## 👥 Contributors

* **[Your Name]** - *Lead Developer*

---

## 📄 License
This project is for educational purposes.
