# Phase 01 — Web Application Boilerplate Setup

## 1. Phase Title and Purpose
**Phase 01 — Web Application Boilerplate Setup**

The goal was to establish a reusable no-framework web application foundation using HTML, CSS, Vanilla JavaScript, Java, Gradle, and MySQL. This serves as the starting point before implementing user registration and authentication.

## 2. Starting State
The user started with an empty or non-existent `G:\Project` directory.
Requirements specified:
- No frameworks allowed (no Spring, Spring Boot, React, etc.).
- Standard Java library for HTTP server.
- Official MySQL JDBC driver.
- Gradle as the build tool.
- Vanilla HTML/CSS/JS for frontend.

## 3. Final Outcome
Phase 1 successfully produced a working foundational web application boilerplate.
Working components:
- Gradle build setup with JUnit 5 and MySQL Connector/J.
- Java application server (`com.sun.net.httpserver.HttpServer`) successfully handling HTTP requests on port 8080 and serving static files.
- `DatabaseConnection` class configured to securely read JDBC credentials from environment variables.
- Vanilla HTML, CSS, and JS frontend templates rendering correctly.
- Application verified to start and serve the frontend locally.

What was NOT completed:
- Database connection was implemented but NOT verified against a running MySQL instance.
- User registration and authentication (deferred).

## 4. Implementation Journey
1. **Project configuration and folder structure:** Created standard Gradle layout and `build.gradle`. Attempted to generate Gradle wrapper.
2. **Java setup issues:** Discovered `JAVA_HOME` configuration was broken on the system, pointing to an invalid `D:\jdk-20.0.1\bin` and conflicting JREs. Attempted several environment variable fixes.
3. **Out of Disk Space:** Discovered the host machine lacked disk space on the `C:\` drive, preventing downloads and Gradle caching.
4. **Isolated Java and Gradle Configuration:** Fixed the environment by downloading a portable Amazon Corretto JDK 21 zip directly to the `G:\` drive (`G:\Project\.jdk`), explicitly configuring Gradle to use it (`gradle.properties`), and directing Gradle's user home to `G:\Project\.gradle-user-home`. Also downloaded the Gradle wrapper zip locally to bypass network SSL failures in the older Java runtime.
5. **Java Application Server (TDD):** Wrote failing tests for root route, then implemented Java's built-in `HttpServer`.
6. **Database Connection setup (TDD):** Wrote failing test for JDBC URL formatting, then implemented `DatabaseConnection.java` to read from environment variables.
7. **Frontend Setup:** Created `index.html`, `style.css`, and `app.js`.
8. **Serve Frontend via Java:** Wrote tests for static file serving, updated `Application.java` to serve static resources from classpath, and return 404 for missing resources.
9. **Environment Configuration & README:** Added `.env.example` and `README.md`.
10. **Verification:** Successfully ran `./gradlew run` and verified the application served content on port 8080.

## 5. Exact Project Structure
Confirmed by the repository:
```text
G:\Project
├── build.gradle
├── settings.gradle
├── gradle.properties
├── README.md
├── .env.example
├── gradle.zip
├── .jdk/
├── .gradle-user-home/
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/webapp/
    │   │       ├── Application.java
    │   │       └── database/
    │   │           └── DatabaseConnection.java
    │   └── resources/
    │       └── static/
    │           ├── index.html
    │           ├── css/
    │           │   └── style.css
    │           └── js/
    │               └── app.js
    └── test/
        └── java/
            └── com/example/webapp/
                ├── ApplicationTest.java
                └── database/
                    └── DatabaseConnectionTest.java
```

## 6. Files Changed
### Created
- `settings.gradle`: Configures root project name.
- `build.gradle`: Configures Java, Application plugin, and dependencies (MySQL JDBC, JUnit).
- `gradle.properties`: Hardcodes Java toolchain path to `.jdk` to bypass OS environment variables.
- `src/main/java/com/example/webapp/Application.java`: Main entry point and built-in HTTP server implementation serving static assets.
- `src/main/java/com/example/webapp/database/DatabaseConnection.java`: Handles JDBC connection construction via environment variables.
- `src/main/resources/static/index.html`: Entry frontend page.
- `src/main/resources/static/css/style.css`: Clean vanilla stylesheet.
- `src/main/resources/static/js/app.js`: Simple DOM loaded initialization script.
- `src/test/java/com/example/webapp/ApplicationTest.java`: TDD integration tests for HTTP server behavior.
- `src/test/java/com/example/webapp/database/DatabaseConnectionTest.java`: Unit tests for JDBC URL formatting.
- `README.md`: Project documentation.
- `.env.example`: Template for local database credentials.

## 7. Architecture
**Browser → Java HTTP Server → JDBC → MySQL**
Currently, the Java HTTP Server (`com.sun.net.httpserver.HttpServer`) listens on port 8080. When a request hits `/`, it maps to `/index.html`. It reads static files from the `src/main/resources/static/` directory in the classpath and serves them with basic MIME types. The database connection logic is separated into `DatabaseConnection.java` which constructs JDBC connections for future data handlers to utilize.

## 8. Java Implementation
- **Java version detected:** Java 21 (Amazon Corretto portable JDK in `.jdk/`).
- **Main application class:** `com.example.webapp.Application`
- **HTTP server implementation:** Uses `com.sun.net.httpserver.HttpServer`.
- **Current routes:**
  - `/` -> serves `/index.html`
  - Fallback serves static resources mapped directly from `src/main/resources/static/`. Returns 404 if not found.
- **Important configuration:** Static files are served via `Application.class.getResourceAsStream("/static" + path)`.

## 9. Frontend Implementation
- **HTML:** `src/main/resources/static/index.html`
- **CSS:** `src/main/resources/static/css/style.css`
- **JavaScript:** `src/main/resources/static/js/app.js`
- **Behavior:** Renders a clean status page indicating the boilerplate foundation is ready and JavaScript executes on DOM load.

## 10. Gradle Configuration
- **Gradle version:** 8.7 (wrapper downloaded via local `gradle.zip` due to network/Java issues).
- **Plugins:** `java`, `application`.
- **Dependencies:**
  - `com.mysql:mysql-connector-j:8.3.0`
  - `org.junit:junit-bom:5.10.2` (platform)
  - `org.junit.jupiter:junit-jupiter`
- **Configuration decisions:** Uses `org.gradle.java.home` in `gradle.properties` to isolate the project's JDK, bypassing unreliable host environment variables. Used `GRADLE_USER_HOME` on `G:\` due to host `C:\` disk space constraints.

## 11. MySQL and JDBC Configuration
- **JDBC Driver:** `com.mysql.cj.jdbc.Driver`
- **Implementation:** `DatabaseConnection.java` constructs connections via `DriverManager.getConnection`.
- **Configuration mechanism:** Environment variables.
- **Environment variables:** `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.
- **Verification:** URL generation logic is verified via unit tests. **Actual MySQL connectivity was configured but NOT verified** (no local database was spun up during this phase).

## 12. Commands and Operations
- `Invoke-WebRequest -Uri "https://corretto.aws/downloads/latest/amazon-corretto-21-x64-windows-jdk.zip" -OutFile "G:\Project\jdk21.zip"`: Bypassed `C:\` space limitations.
- `Expand-Archive -Path "G:\Project\jdk21.zip" -DestinationPath "G:\Project\.jdk" -Force`: Set up portable Java.
- `$env:GRADLE_USER_HOME = "G:\Project\.gradle-user-home"`: Prevented Gradle from extracting to the full `C:\` drive.
- `.\gradlew test`: Executed TDD cycles.
- `.\gradlew run`: Started the backend server successfully.

## 13. Failures, Debugging, and Fixes
1. **Error:** `JAVA_HOME is set to an invalid directory: D:\jdk-20.0.1\bin`
   **Cause:** Misconfigured OS variables.
   **Fix:** Created `gradle.properties` to hardcode `org.gradle.java.home` to a localized portable JDK.
2. **Error:** `There is not enough space on the disk` on `C:\`.
   **Cause:** Attempted `Invoke-WebRequest` to `$env:TEMP` and Gradle extraction to `C:\Users\...\.gradle` failed.
   **Fix:** Downloaded all binaries (Java, Gradle) directly to `G:\Project` and changed `$env:GRADLE_USER_HOME`.
3. **Error:** `Connection reset by peer` when wrapper attempted to download Gradle.
   **Cause:** Suspected proxy/firewall issue or legacy JRE SSL handshake failure.
   **Fix:** Downloaded `gradle-8.7-bin.zip` via PowerShell `Invoke-WebRequest` and updated `gradle-wrapper.properties` to `distributionUrl=file\:///G:/Project/gradle.zip`.

## 14. Tests and Verification
- **Verified:** `DatabaseConnectionTest.java` successfully tests that the JDBC URL is correctly formatted from given credentials (Pass).
- **Verified:** `ApplicationTest.java` successfully tests that `/` serves the index HTML content and missing routes return 404 (Pass).
- **Verified:** Project builds and runs via `./gradlew run`.
- **Configured but not verified:** Actual database connectivity to a running MySQL instance was not tested.

## 15. Architecture and Design Decisions
- **No frameworks:** Enforced vanilla Java HTTP Server and Vanilla JS/HTML/CSS.
- **Portable isolated tooling:** Decided to embed the JDK and Gradle cache inside `G:\Project` directly to mitigate the host's disk space and path corruption issues.

## 16. Security / Operational Considerations
- **Credentials:** No database credentials are hardcoded. They are safely extracted from environment variables at runtime.
- **File System Serving:** `Application.java` blindly loads resources from the classpath if they exist under `/static`. Path traversal could technically be a concern if the request URI is manipulated, though `getResourceAsStream` restricts resolution to the classpath contents.
- **Limitations:** The host environment is extremely fragile due to no disk space on `C:\`. Future scripts should continue to use `G:\` exclusively.

## 17. Deferred / Out-of-Scope Work
Intentionally NOT implemented:
- User registration
- User database schema/tables
- Password hashing
- Login / Authentication
- Sessions or authorization

## 18. Known Limitations and Unresolved Questions
- **Unverified configuration:** MySQL connection is configured but unverified. The next phase will need to establish an actual MySQL instance to verify connectivity.
- **Environment-specific assumptions:** The `GRADLE_USER_HOME` environment variable must be set to `G:\Project\.gradle-user-home` manually if the user opens a new terminal session, otherwise Gradle will fail trying to use `C:\`.

## 19. Git / Commit Linkage
**Phase 1 is not committed.**
Git is not installed or configured on the host machine (`git : The term 'git' is not recognized`).

## 20. Resume From Here
The project is in a successfully compiling, verified state with a working HTTP server serving a static frontend template. The project is completely self-contained on the `G:\` drive to avoid Windows environment variable and disk space issues on the `C:\` drive.

The next planned phase is:
**User Registration**

The next agent should first read this Phase 1 context and inspect the repository before implementing anything. The next step will require handling POST requests, setting up a MySQL database, and creating the initial user tables.
