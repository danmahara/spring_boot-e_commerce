# 🛒 E-Commerce Web Application

A full-featured e-commerce web application built with **Spring Boot**, featuring role-based authentication, product management, cart & order workflows, image uploads, and database migrations.

---

## ✨ Features

- 🔐 **Authentication & Authorization** — secure login/register with role-based access control (Admin / Customer) using Spring Security
- 🛍️ **Product Management** — admins can add, edit, and delete products with image upload support
- 🛒 **Shopping Cart** — add/remove items, update quantities, persistent cart per user
- 📦 **Order Management** — place orders, view order history, admin order status updates
- 🖼️ **Image Uploads** — supports JPEG, PNG, GIF, WebP up to 20MB via multipart upload
- 🗄️ **Database Migrations** — versioned schema management with Flyway
- 📱 **Responsive UI** — built with Thymeleaf templates, HTML, CSS and JavaScript

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java, Spring Boot |
| Security | Spring Security, session-based auth |
| ORM | Spring Data JPA, Hibernate |
| Database | MySQL |
| Migrations | Flyway |
| Frontend | Thymeleaf, HTML, CSS, JavaScript |
| Build | Maven |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/danmahara/spring_boot-e_commerce.git
   cd spring_boot-e_commerce
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE ecommerce_spring;
   ```

3. **Configure application properties**

   Edit `src/main/resources/application.properties` and update:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run initial SQL scripts**
   ```bash
   mysql -u your_username -p ecommerce_spring < roles_permissions.sql
   mysql -u your_username -p ecommerce_spring < orders.sql
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

6. **Open in browser**
   ```
   http://localhost:8080
   ```

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/ecommerce/
│   │       ├── controllers/
│   │       │   └── admin/      # Admin-specific controllers
│   │       ├── models/         # JPA entities
│   │       ├── repositories/   # Spring Data repositories
│   │       ├── services/       # Business logic
│   │       └── config/         # Spring Security & app config
│   └── resources/
│       ├── templates/          # Thymeleaf HTML templates
│       ├── static/             # CSS, JS, images
│       ├── db/migration/       # Flyway SQL migration files
│       └── application.properties
```

---

## 🔑 Default Admin Credentials

| Field | Value |
|-------|-------|
| Email | `admin@gmail.com` |
| Password | `123456789` |

> ⚠️ Change these credentials before deploying to production.

---

## 🖼️ Image Upload Config

| Setting | Value |
|---------|-------|
| Max file size | 20MB |
| Allowed types | JPEG, PNG, GIF, WebP |
| Upload directory | `uploads/` |

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

**Dan Mahara**
- Portfolio: [danmahara.com.np](https://danmahara.com.np)
- LinkedIn: [linkedin.com/in/dan-mahara-1a2846280](https://www.linkedin.com/in/dan-mahara-1a2846280/)
- GitHub: [@danmahara](https://github.com/danmahara)
