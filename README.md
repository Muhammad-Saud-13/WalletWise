# 💰 WalletWise — Personal Finance Tracker with AI Insights

> A production-ready RESTful backend built with **Java Spring Boot** and **MongoDB**, featuring JWT authentication, smart budgeting, AI-powered spending insights, automated reporting, and third-party integrations.

---

## 🚧 Project Status

| Module | Status |
|---|---|
| Authentication (Register, Login, JWT) | ✅ Complete |
| Transaction Management (CRUD + Filters) | ✅ Complete |
| Budget Management | 🔄 In Progress |
| Reports & Summary | 🔄 In Progress |
| AI Spending Insights (Gemini API) | 🔄 In Progress |
| Email Notifications (SendGrid) | 🔄 In Progress |
| Scheduled Jobs | 🔄 In Progress |
| CSV Import / Export | 🔄 In Progress |
| Admin Module | 🔄 In Progress |
| Swagger / OpenAPI Docs | 🔄 In Progress |

---

## 📌 Overview

WalletWise is a backend system that helps users take control of their personal finances. Users can track income and expenses, set monthly budgets per category, and receive AI-generated insights on their spending habits — all through a clean, well-documented REST API.


---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| Security | Spring Security + JWT |
| Database | MongoDB (Spring Data MongoDB) |
| Email Service | SendGrid API |
| AI Integration | Google Gemini API |
| Build Tool | Maven |
| Testing | JUnit 5 + Mockito |
| API Documentation | Swagger / OpenAPI 3 |

---

## ✨ Features

### 🔐 Authentication & Security
- User registration and login with JWT-based authentication
- Role-based access control — `USER` and `ADMIN` roles
- Password reset via email with secure token
- All endpoints protected except `/auth/register` and `/auth/login`

### 💸 Transaction Management
- Add, update, delete, and retrieve transactions
- Transaction types: `INCOME` and `EXPENSE`
- Categories: Food, Salary, Rent, Transport, Entertainment, and more
- Payment methods: Cash, Card, Bank Transfer
- Filter transactions by date range, category, and type
- Pagination support on transaction listing
- CSV import — bulk upload transactions via CSV file
- CSV export — download all transactions as a formatted CSV

### 📊 Budget Management *(In Progress)*
- Set monthly budget limits per category
- Real-time tracking of spending vs budget
- Automated alert when spending reaches 80% of budget limit

### 📈 Reports & Summary *(In Progress)*
- Monthly income, expenses, and net savings summary
- Category-wise spending breakdown
- Month-over-month comparison
- Top 5 spending categories
- Daily spending trend for current month

### 🤖 AI Insights — Gemini API *(In Progress)*
- Personalized saving tips based on last 30 days of transactions
- Unusual spending pattern detection
- AI-recommended budget limits per category
- Monthly financial health score (1–100) with explanation

### 📧 Notifications & Scheduling *(In Progress)*
- Welcome email on registration
- Password reset email with secure link
- Budget alert email when category spending exceeds 80%
- Automated monthly financial summary emailed to all users on the 1st of each month
- Daily background job to check and trigger budget alerts

### 🛡️ Admin Module *(In Progress)*
- View and manage all users
- Deactivate or reactivate user accounts
- Platform-wide statistics: total users, total transactions, most used categories

---

## 🗂️ Project Structure

```
walletwise/
├── src/
│   └── main/
│       ├── java/com/walletwise/
│       │   ├── auth/               # Authentication, JWT, Spring Security
│       │   ├── user/               # User profile management
│       │   ├── transaction/        # Transaction CRUD, filters, CSV
│       │   ├── budget/             # Budget management and alerts
│       │   ├── report/             # Summary and report generation
│       │   ├── ai/                 # Gemini API integration
│       │   ├── notification/       # SendGrid email service
│       │   ├── scheduler/          # Spring @Scheduled background jobs
│       │   └── admin/              # Admin endpoints
│       └── resources/
│           └── application.properties
├── pom.xml
└── README.md
```

---

## 🗄️ Database Schema

### `users` Collection
```json
{
  "id": "string",
  "fullName": "string",
  "email": "string",
  "password": "string (bcrypt hashed)",
  "role": "USER | ADMIN",
  "isActive": "boolean",
  "currency": "PKR | USD | EUR",
  "createdAt": "datetime"
}
```

### `transactions` Collection
```json
{
  "id": "string",
  "userId": "string",
  "type": "INCOME | EXPENSE",
  "amount": "number",
  "category": "string",
  "description": "string",
  "date": "date",
  "paymentMethod": "Cash | Card | Bank Transfer",
  "createdAt": "datetime"
}
```

### `budgets` Collection
```json
{
  "id": "string",
  "userId": "string",
  "category": "string",
  "monthlyLimit": "number",
  "month": "string (YYYY-MM)",
  "createdAt": "datetime"
}
```

---

## 🔌 API Endpoints

### Auth
```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
PUT    /api/auth/change-password
```

### Transactions
```
POST   /api/transactions
GET    /api/transactions          ?type=&category=&from=&to=&page=&size=
GET    /api/transactions/{id}
PUT    /api/transactions/{id}
DELETE /api/transactions/{id}
POST   /api/transactions/import-csv
GET    /api/transactions/export-csv
```

### Budgets *(In Progress)*
```
POST   /api/budgets
GET    /api/budgets
PUT    /api/budgets/{id}
DELETE /api/budgets/{id}
GET    /api/budgets/status
```

### Reports *(In Progress)*
```
GET    /api/reports/monthly-summary?month=2026-04
GET    /api/reports/category-breakdown?month=2026-04
GET    /api/reports/monthly-comparison
GET    /api/reports/top-categories
GET    /api/reports/daily-trend
```

### AI Insights *(In Progress)*
```
GET    /api/ai/spending-insights
GET    /api/ai/budget-recommendations
GET    /api/ai/health-score
```

### Admin *(In Progress)*
```
GET    /api/admin/users
PUT    /api/admin/users/{id}/deactivate
PUT    /api/admin/users/{id}/reactivate
GET    /api/admin/stats
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MongoDB (local or MongoDB Atlas)
- SendGrid API Key (free tier)
- Google Gemini API Key (free tier)

### Setup

1. **Clone the repository**
```bash
git clone https://github.com/Muhammad-Saud-13/walletwise.git
cd walletwise
```

2. **Configure environment variables**

Create an `application.properties` file or set the following environment variables:

```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/walletwise

# JWT
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# SendGrid
sendgrid.api.key=your_sendgrid_api_key
sendgrid.from.email=your_email@example.com

# Gemini AI
gemini.api.key=your_gemini_api_key
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access Swagger UI**
```
http://localhost:8080/swagger-ui.html
```

---

## 📥 CSV Import Format

When importing transactions via CSV, use the following format:

```
date,type,amount,category,description,paymentMethod
2026-04-01,EXPENSE,500,Food,Lunch,Cash
2026-04-02,INCOME,50000,Salary,Monthly salary,Bank Transfer
2026-04-03,EXPENSE,2000,Transport,Uber,Card
```

- `date` — format: `YYYY-MM-DD`
- `type` — `INCOME` or `EXPENSE`
- `paymentMethod` — `Cash`, `Card`, or `Bank Transfer`
- Invalid rows are skipped and reported in the response

---

## 🧪 Testing

Unit tests written with JUnit 5 and Mockito covering core service logic.

```bash
mvn test
```

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

**Muhammad Saud**
- GitHub: [@Muhammad-Saud-13](https://github.com/Muhammad-Saud-13)
- LinkedIn: [linkedin.com/in/muhammadsaud002](https://linkedin.com/in/muhammadsaud002)
- Email: saudkhanlahore@gmail.com