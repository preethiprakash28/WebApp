# Phase 02 — User Registration

## 1. Phase Title and Purpose
**Phase 02 — User Registration**

Purpose: Implement the first real application feature that accepts user registration data (name, phone, email, and password) and persists it through the Java HTTP Server and JDBC to a local database.

## 2. Starting State
Phase 1 established the foundation for a framework-free web application.
- **Frontend:** Vanilla HTML, CSS, JS serving a placeholder page.
- **Backend:** Java `HttpServer` (standard library) running on port 8080.
- **Database:** Isolated configuration for MySQL/MariaDB and JDBC, but no tables or verified connection existed yet.
- **Build:** Gradle with a customized localized `.gradle-user-home` and `.jdk` directory due to host constraints.
Assumptions entering Phase 2 were that the host's `C:\` drive was still completely full, necessitating portable database alternatives.

## 3. Final Outcome
Phase 2 successfully implemented end-to-end user registration.
- **Registration Form:** An HTML form capturing the 4 required fields.
- **JavaScript Submission:** Uses `fetch()` with `URLSearchParams` to submit `application/x-www-form-urlencoded` data to the backend without reloading the page.
- **Registration Endpoint:** `POST /api/register` captures, parses, and validates the HTTP request.
- **Password Hashing:** Implemented `jbcrypt` to securely salt and hash the plaintext password before storage.
- **Database Persistence:** Using a `PreparedStatement` to safely execute an `INSERT` statement via JDBC.
- **Duplicate Email Handling:** The database enforces a `UNIQUE` constraint on the `email` column, and the Java backend translates `SQLException` into HTTP `400 Bad Request`.
- **Verification:** The backend logic and database insertion were proven to work via `UserRepositoryTest` (which passed against the live portable database).

## 4. Implementation Journey
1. **Dependency & Database Schema Setup:** Added `org.mindrot:jbcrypt:0.4` to `build.gradle`. Since a standard MySQL installation was impossible due to disk space limitations on `C:\`, a portable **MariaDB 10.11.7** distribution was downloaded via PowerShell directly to `G:\mysql-server`. A `users` table was then manually created via the `mysql.exe` client.
2. **User Repository & Password Hashing (TDD):** Wrote a failing integration test (`UserRepositoryTest`) simulating a registration insertion. Implemented `UserRepository.java` using JDBC and BCrypt to make the test pass.
3. **Registration HTTP Endpoint (TDD):** Wrote a failing test (`ApplicationTest.testRegistrationEndpoint`) expecting a `201 Created` response. Implemented `POST /api/register` inside `Application.java` using raw string manipulation to decode URL-encoded form data.
4. **Frontend HTML & Vanilla JS:** Replaced the placeholder HTML in `index.html` with a registration form. Updated `App.js` to intercept form submission, send a `fetch()` request, and display success/error boxes.
5. **Full System Verification:** Instructed the user to run `./gradlew run` and open `http://localhost:8080` to verify the end-to-end flow.

## 5. Files Created
- `src/main/java/com/example/webapp/database/UserRepository.java`: Handles backend validation, BCrypt password hashing, and the JDBC `INSERT` statement.
- `src/test/java/com/example/webapp/database/UserRepositoryTest.java`: Integration test to verify that `UserRepository.saveUser()` successfully hashes a password and inserts a row into the database.

## 6. Files Modified
- `build.gradle`: Added the `jbcrypt` dependency.
- `src/main/java/com/example/webapp/Application.java`: Added a new HTTP context handler for `POST /api/register` to parse request bodies and invoke the repository.
- `src/main/resources/static/index.html`: Added the registration HTML `<form>` with the four required inputs.
- `src/main/resources/static/css/style.css`: Added minimal styling for the form elements.
- `src/main/resources/static/js/App.js`: Replaced the placeholder script with a robust `fetch()` POST mechanism handling `application/x-www-form-urlencoded` payloads and UI state updates.
- `src/test/java/com/example/webapp/ApplicationTest.java`: Added an HTTP integration test for the `/api/register` endpoint.

## 7. Registration Architecture
```text
HTML Form
   ↓
Vanilla JavaScript (fetch)
   ↓
POST /api/register
   ↓
Java HTTP Server (com.sun.net.httpserver)
   ↓
Request parsing & validation
   ↓
Password hashing (BCrypt)
   ↓
UserRepository (saveUser)
   ↓
JDBC (PreparedStatement)
   ↓
Database (MariaDB users table)
```
The frontend gathers data using the `FormData` API, converts it into a URL-encoded string using `URLSearchParams`, and sends it via `fetch` with the `Content-Type: application/x-www-form-urlencoded` header.

## 8. Registration Endpoint
- **HTTP Method:** `POST`
- **Endpoint:** `/api/register`
- **Request Format:** `application/x-www-form-urlencoded`
- **Expected Fields:** `name`, `phone`, `email`, `password`
- **Validation:** Verifies all fields are present and non-empty. Database enforces email uniqueness.
- **Response Behavior:** Returns plain text confirmation or error messages.
- **HTTP Status Codes:**
  - `201 Created` for successful registration.
  - `400 Bad Request` for validation failure or duplicate email.
  - `405 Method Not Allowed` for non-POST requests.
  - `500 Internal Server Error` for unexpected crashes.

## 9. Frontend Implementation
- **Location:** `src/main/resources/static/index.html` and `src/main/resources/static/js/App.js`
- **Fields:** Full Name (`type="text"`), Phone Number (`type="tel"`), Email Address (`type="email"`), Password (`type="password"`, `minlength="6"`). All inputs are marked `required`.
- **Submission:** A `submit` event listener prevents default browser behavior. The submit button is disabled and text is changed to "Registering..." during the request to prevent double-clicks.
- **Response Handling:** A dedicated `<div>` is unhidden to display either green success text or red error text depending on the `response.ok` property. The form is reset on success.

## 10. Database Implementation
- **Database Engine Detected:** MariaDB 10.11.7 (Portable zip execution).
- **JDBC Driver:** `com.mysql.cj.jdbc.Driver` (MySQL Connector/J 8.3.0). MariaDB is fully compatible with this driver.
- **Database Name:** `webapp`
- **Table Definition (`users`):**
  - `id INT AUTO_INCREMENT PRIMARY KEY`
  - `name VARCHAR(255) NOT NULL`
  - `phone VARCHAR(50) NOT NULL`
  - `email VARCHAR(255) NOT NULL UNIQUE` (Enforces no duplicate registrations)
  - `password VARCHAR(255) NOT NULL` (Stores the BCrypt hash, NOT plaintext)
  - `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`

*Note: The password column is literally named `password`, but it stores the BCrypt hash. This matches the intended schema design.*

## 11. JDBC Implementation
- **Connection Management:** Reuses the `DatabaseConnection` class built in Phase 1, securely pulling credentials from environment variables (`DB_HOST`, `DB_USER`, etc.).
- **SQL Execution:** Uses a `PreparedStatement` (`INSERT INTO users (name, phone, email, password) VALUES (?, ?, ?, ?)`) to strictly bind parameters, completely eliminating SQL injection risks.
- **Error Handling:** Catches `SQLException`. Because a duplicate email violates the `UNIQUE` constraint, it triggers an exception, and the repository safely returns `false` (handled as a 400 response by the API).

## 12. Password Security
- **Dependency:** `org.mindrot:jbcrypt:0.4`
- **Mechanism:** Passwords are hashed using `BCrypt.hashpw(password, BCrypt.gensalt())`.
- **Storage:** Only the resulting salted hash string is stored in the database.
- **Exposure:** Raw passwords are not printed to standard out, logged, or returned in HTTP responses.

## 13. Tests and TDD
- `UserRepositoryTest.testSaveUser`: **Executed and Passed**. Connects to the real portable MariaDB instance, successfully hashes a test password, and inserts a user with a dynamic email address.
- `ApplicationTest.testRegistrationEndpoint`: **Executed and Passed**. Sends an actual `POST` request to the locally bound Java `HttpServer` testing the HTTP parsing logic and expecting a `201 Created` status code.

## 14. Build Verification
- **Command:** `.\gradlew test`
- **Result:** Successfully compiled and executed. The repository confirms both tests pass in the local environment.

## 15. Full System Verification
The user was instructed to verify the end-to-end functionality via the browser (`http://localhost:8080`), but the chat history ended before explicit confirmation of manual browser testing was provided. However, the automated integration tests confirming the backend and database flow passed successfully.

## 16. Failures, Debugging, and Fixes
- **Problem:** Host `C:\` drive completely out of disk space, making standard MySQL Installer usage impossible.
- **Fix:** Provided a PowerShell script to download the `mariadb-10.11.7-winx64.zip` distribution directly to the `G:\` drive, extracted it, initialized the data directory via `mysql_install_db.exe`, and ran `mysqld.exe` in the background.

## 17. Dependencies
- `org.mindrot:jbcrypt:0.4`: Required to securely hash passwords before database insertion without relying on massive authentication frameworks.
- `com.mysql:mysql-connector-j:8.3.0`: (From Phase 1) Required for the JDBC `Connection` and `PreparedStatement` functionality.

## 18. Security Considerations
- Passwords are securely hashed with BCrypt.
- SQL Injection is prevented via `PreparedStatement`.
- Database credentials are not hardcoded.
- However, the `Application.java` CORS/CSRF protections do not exist, and there is no rate-limiting, which is standard for a purely educational boilerplate.

## 19. Known Issues / Contradictions
**Important Database Discrepancy:** The prompt and Phase 1 documentation dictated **MySQL**. However, due to the severe disk space constraints on the user's `C:\` drive preventing the standard MySQL installer from executing, **MariaDB** (a portable drop-in replacement fork) was downloaded and run from the `G:\` drive instead.
- **Resolution:** The implementation uses the official MySQL JDBC Driver (`mysql-connector-j`), and the MariaDB server accepts the connection and executes the queries identically. This workaround was successful and verified by passing integration tests.

## 20. Deferred Work
Intentionally NOT implemented:
- Login
- Logout
- Sessions
- JWT
- Password reset
- Email verification
- OTP
- User profile
- Authorization
- Roles/permissions

## 21. Final Repository State
The repository contains a fully functioning standard-library Java backend listening on port 8080. It serves static HTML/JS/CSS assets and exposes a `/api/register` endpoint that securely connects to a background MariaDB instance running from `G:\mysql-server`.

## 22. Commit Linkage
**Phase 2 is not committed.**
Due to the terminal crashing/resetting immediately prior to inspection, `git` was inaccessible in the system PATH, and no evidence of a Phase 2 commit exists yet.

## 23. Resume From Here
- Phase 2 registration is the completed implementation boundary.
- The next phase must begin by reading this Phase 2 context.
- The next implementation should inspect the repository before making changes.
- Login/authentication has NOT been implemented.
- Do not assume browser-level verification was completed unless confirmed.
- Any unresolved MySQL/MariaDB discrepancy must be resolved based on actual repository/environment evidence before relying on it.
