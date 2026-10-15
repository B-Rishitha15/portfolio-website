# Developer Portfolio Website

My personal portfolio website, built with **Java, Spring Boot and Thymeleaf**. It showcases my skills, projects, education, certificates and resume, and has a working contact form that emails messages to me.

**Live demo:** coming soon

## Features

- Responsive, dark-themed UI built with Tailwind CSS
- Interactive elements: typing effect, particle background, scroll animations, 3D tilt cards, skills filter and animated counters
- Projects loaded dynamically from a database using Spring Data JPA and Thymeleaf
- Resume section with view and download options
- Contact form with server-side validation, honeypot spam protection and email delivery
- Email sent through the Resend API in production, with Gmail SMTP as a local fallback
- Secrets (database password, API keys) kept in environment variables, never in the code
- Docker support for deployment

## Tech Stack

| Area | Technology |
|------|------------|
| Backend | Java 21, Spring Boot 3.3.5, Spring Web, Spring Data JPA |
| Frontend | Thymeleaf, Tailwind CSS (CDN), JavaScript |
| Database | MySQL (local), PostgreSQL (production) |
| Email | Resend API, Spring Boot Mail (JavaMailSender) |
| Build and deploy | Maven, Docker, Git and GitHub |

## Project Structure

```text
src/main
├── java/com/portfolio
│   ├── config         # Seeds the projects on startup (DataInitializer)
│   ├── controller     # Page and contact form endpoints
│   ├── dto            # Contact form request with validation
│   ├── exception      # Custom exceptions
│   ├── model          # Project entity
│   ├── repository     # Spring Data JPA repositories
│   └── service        # Project and contact (email) services
└── resources
    ├── static         # CSS, JavaScript, resume PDF
    ├── templates      # index.html (Thymeleaf)
    └── application*.properties   # local and production profiles
```

## Run Locally

### Prerequisites

- Java 21
- Maven
- MySQL running on your machine

### Steps

1. Clone the repository:

```bash
   git clone https://github.com/B-Rishitha15/portfolio-website.git
   cd portfolio-website
```

2. Check the MySQL port in `src/main/resources/application-local.properties` and change it if yours is different. The database `portfolio_db` is created automatically.

3. Set your environment variables (PowerShell example):

```powershell
   $env:DB_PASSWORD="your-mysql-password"
```

   To test the contact form, also set one of these:

```powershell
   # Option A: Resend API
   $env:RESEND_API_KEY="your-resend-api-key"
   $env:CONTACT_RECIPIENT="you@example.com"

   # Option B: Gmail SMTP (use a Gmail App Password)
   $env:MAIL_USERNAME="you@gmail.com"
   $env:MAIL_PASSWORD="your-app-password"
```

4. Run the application:

```bash
   mvn spring-boot:run
```

5. Open http://localhost:8080

## Run with Docker

```bash
docker build -t portfolio .
docker run -p 8080:8080 \
  -e DB_HOST=your-db-host -e DB_NAME=your-db-name \
  -e DB_USERNAME=your-db-user -e DB_PASSWORD=your-db-password \
  portfolio
```

The Docker image uses the `prod` profile, which connects to PostgreSQL.

## Environment Variables

| Variable | Purpose |
|----------|---------|
| `DB_PASSWORD` | Database password (local and production) |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` | PostgreSQL connection (production) |
| `RESEND_API_KEY` | Resend API key for sending contact emails |
| `CONTACT_RECIPIENT` | Email address that receives contact form messages |
| `MAIL_USERNAME`, `MAIL_PASSWORD` | Gmail SMTP login (local fallback) |

## About Me

**Batchu Rishitha**, B.Tech Computer Science student and Java developer (fresher), interested in Spring Boot and AI.

- LinkedIn: https://www.linkedin.com/in/batchu-rishitha
- GitHub: https://github.com/B-Rishitha15