# FinSight - AI Expense Tracker

FinSight is an intelligent expense tracking application that leverages the power of Local AI (Llama 3 via Ollama) to automatically categorize your financial transactions. Built with Spring Boot 3, it offers a secure, fast, and intelligent way to manage your expenses.

## 🚀 Features

- **Smart Categorization:** Uses an integrated local LLM (Ollama) to automatically categorize expenses based on their description.
- **Secure Authentication:** JSON Web Token (JWT) based authentication for secure access.
- **Modern Backend:** Built on Spring Boot 3.4.0 with Java 24.
- **Database:** PostgreSQL for persistent data storage.
- **Caching:** Redis integration for fast access and caching categorization results.
- **API Documentation:** Interactive Swagger UI for easy API exploration and testing.
- **Containerized Services:** Docker Compose setup for instant PostgreSQL and Redis provisioning.

## 🛠️ Technology Stack

- **Java 24**
- **Spring Boot 3.4.0**
- **Spring Security & JWT**
- **Spring AI (Ollama)**
- **PostgreSQL**
- **Redis**
- **Docker Compose**
- **Swagger / OpenAPI**

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
1. **Java 24**
2. **Docker Desktop** (Required for PostgreSQL and Redis via Docker Compose)
3. **Ollama** (Required for local AI categorization)
   - Ensure you have pulled the `llama3` model: `ollama run llama3`
   - Make sure Ollama is running on `localhost:11434`

## ⚙️ Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/jul2264/FinSight.git
cd FinSight/ai-expense-tracker
```

### 2. Run the application
Spring Boot's Docker Compose support will automatically spin up the required PostgreSQL and Redis containers for you!

```bash
./mvnw clean spring-boot:run
```

### 3. Access the API Documentation
Once the server is running, you can explore the API using the built-in Swagger UI:
- **URL:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🔐 How to Test the API

1. Go to the Swagger UI.
2. Under `auth-controller`, use `POST /api/auth/register` to create a new user.
3. Use `POST /api/auth/login` with your credentials to obtain a JWT.
4. Click the green **Authorize** button at the top of the Swagger page and paste your token.
5. You can now use the `expense-controller` to `POST /api/expenses`. Add a description like *"bought groceries at whole foods"* and watch the AI automatically categorize it!

## 📄 License
This project is open-source and available under the MIT License.
