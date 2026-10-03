# Full-Stack Web Application Boilerplate

## 1. Project Purpose
A clean, minimal, and reusable foundation for building a full-stack web application. Note: **Authentication will be implemented in a later step.**

## 2. Technology Stack
- **Frontend:** HTML5, CSS3, Vanilla JavaScript (No frameworks)
- **Backend:** Java 21 (Standard Library HTTP Server)
- **Database:** MySQL
- **Build Tool:** Gradle

## 3. Project Structure
- `src/main/java`: Contains the Java backend server (`Application.java`) and database connection code.
- `src/main/resources/static`: Contains the frontend HTML, CSS, and JS files served to the browser.
- `src/test/java`: Contains our JUnit 5 Test-Driven Development test suite.

## 4. Java Requirements
- Java 21+ is required to build and run the application.

## 5. Gradle Requirements
- Managed via the included Gradle Wrapper (`gradlew`). No global installation needed.

## 6. MySQL Requirements
- A local MySQL server running on port 3306.

## 7. MySQL Database Setup
- Create a database (e.g., `webapp`).
- Ensure you have a user with access to that database.
- (User tables and schemas will be created when authentication is implemented later).

## 8. Environment Variables
Copy `.env.example` to your system environment variables before running the application:
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

## 9. How to Build
Run all unit tests and compile the project:
`./gradlew build`

## 10. How to Run
Start the development server:
`./gradlew run`

## 11. Local Application URL
Once running, open your browser to: `http://localhost:8080`

## 12. How the Java application connects to MySQL
The backend uses pure JDBC via the `DatabaseConnection` class. It safely reads credentials from environment variables to establish the connection, avoiding hardcoded passwords and ORM overhead.