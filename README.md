# E-Shop Backend

This is the backend of the **E-Shop** project, built with **Spring Boot**, **Spring Security**, **JWT Authentication**, and **MySQL**. It provides a REST API for managing users, roles, products, and authentication logic for an e-commerce platform.

---

## 🛠 Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- ModelMapper
- MySQL
- Maven
- IntelliJ IDEA / VS Code

---

## 📁 Project Structure

<pre><code>src/
e-shop-backend/
├── src/
│ ├── main/
│ │ ├── java/com/eshop/
│ │ │ ├── config/ # Security and Web Config
│ │ │ ├── controller/ # REST Controllers
│ │ │ ├── dto/ # Data Transfer Objects
│ │ │ ├── model/ # JPA Entities (User, Role, Product, etc.)
│ │ │ ├── repository/ # Spring Data JPA Repositories
│ │ │ ├── request/ # Requests specifics to some apis
│ │ │ ├── response/ # ApiResponse/JwtResponse
│ │ │ ├── security/ # Spring Security
│ │ │ ├── service/ # Business Logic and Interfaces
│ │ │ └── EshopApplication.java # Main Application
│ │ └── resources/
│ │ ├── application.properties
│ │ └── static/, templates/
│ └── test/
│ └── ... # Unit and Integration Tests
└── pom.xml
</code></pre>


---

## 🔐 Authentication

- JWT is used for stateless authentication.
- Users sign in via `/signin` and receive a token.
- Use the token in the `Authorization` header:  



---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- MySQL 8.x
- Maven

### 1. Clone the repository

```bash

git clone https://github.com/your-username/e-shop-backend.git
cd e-shop-backend
```
---

### 2. Configure MySQL connection
<pre>
spring.datasource.url=jdbc:mysql://localhost:3306/db_name
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
</pre>

---

### 3. Run the application

```bash
./mvnw spring-boot:run
```
---

🧪 API Endpoints

Here are some core endpoints:
Authentication

    POST /signin – Login with username and password

    POST /signup – Register new users (if enabled)

User Profile

    GET /profile – Get current user info (secured)

    PUT /profile – Update user info (secured)

Admin (optional future scope)

    GET /admin/users – List users

    DELETE /admin/users/{id} – Delete user

    You can test endpoints using Postman or a frontend like E-Shop Frontend (React + Vite) if available.

👤 User Roles

The system supports roles for access control:

    USER – Regular authenticated users

    ADMIN – Admin-level users with management permissions

🖼 Avatar Support

    Users can upload avatars.

    Avatars are handled via base64 or file upload (frontend logic).

    Defaults to a placeholder image if none is provided.

✍️ Author

Developed by [Your Name].