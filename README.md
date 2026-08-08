# Stoles Inventory - Fullstack v3

Spring Boot + React application for managing ladies stoles inventory, worker dispatches, receipts, and payments.

## Tech Stack

- Backend: Spring Boot 3.1.6, Java 17, Spring Security, JWT, Spring Data JPA
- Database: H2 (default, file-based) with optional MySQL configuration
- Frontend: React 18 (`frontend/`), bundled into Spring Boot static assets during Maven build

## Project Layout

```text
stoles-backend/
|- pom.xml
|- README.md
|- frontend/                          # React app (dev server on :3000)
|  |- public/
|  `- src/
|      |- App.jsx                     # Root — state, data loading, routing only
|      |- constants.js                # Domain constants (labels, fabrics, tab lists, icons)
|      |- utils.js                    # Pure helpers (today, fmt, fmtRs, statusColor)
|      |- styles.js                   # Shared inline-style tokens (S object)
|      |- index.js
|      |- api/
|      |  `- api.js                   # All fetch calls to the Spring Boot API
|      `- components/
|          |- LoginScreen.jsx
|          |- Header.jsx
|          |- ui/                     # Primitive, stateless UI components
|          |  |- Badge.jsx
|          |  |- StatCard.jsx
|          |  |- Modal.jsx
|          |  |- FormField.jsx
|          |  |- SelectField.jsx
|          |  |- InputField.jsx
|          |  |- CreatedByCell.jsx
|          |  |- Loader.jsx
|          |  |- ErrMsg.jsx
|          |  `- ModalFooter.jsx
|          |- tabs/                   # One file per tab view
|          |  |- DashboardTab.jsx
|          |  |- StockTab.jsx
|          |  |- DispatchTab.jsx
|          |  |- ReceiveTab.jsx
|          |  |- WorkersTab.jsx
|          |  |- AccountsTab.jsx
|          |  |- ReportsTab.jsx
|          |  `- UsersTab.jsx
|          `- modals/                 # One file per modal
|              |- AddStockModal.jsx
|              |- DispatchModal.jsx
|              |- ReceiveModal.jsx
|              |- PaymentModal.jsx
|              |- AddUserModal.jsx
|              `- ResetPwdModal.jsx
|- postman/                           # API collections/environment
`- src/main/
    |- java/com/stoles/inventory/
    |  |- config/                     # Security, JWT, CORS, data seeding
    |  |- controller/                 # REST endpoints
    |  |- dto/                        # Request/response DTOs
    |  |- service/                    # Business logic
    |  |- repository/                 # JPA repositories
    |  |- entity/                     # JPA entities
    |  |- security/                   # JWT filter, utils, UserDetailsService
    |  `- exception/                  # Global exception handler
    `- resources/
        |- application.properties
        `- init.sql
```

## Frontend Architecture

`App.jsx` is the single orchestrator — it owns all state, fetches data per tab, and routes to tab/modal components via props. It does not contain any rendering logic itself.

| Layer         | Location             | Responsibility                                                  |
|---------------|----------------------|-----------------------------------------------------------------|
| Constants     | `src/constants.js`   | Domain enums and tab config (labels, fabrics, tab lists, icons) |
| Utilities     | `src/utils.js`       | Pure formatting/helper functions with no React dependency       |
| Styles        | `src/styles.js`      | Shared inline-style tokens imported wherever needed             |
| UI primitives | `components/ui/`     | Stateless presentational atoms (Badge, Modal, inputs, etc.)     |
| Tab views     | `components/tabs/`   | One component per tab; receives data and callbacks as props     |
| Modals        | `components/modals/` | One component per modal; owns its own `setF` helper locally     |
| API           | `api/api.js`         | All `fetch` calls to the Spring Boot backend — no UI logic      |

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+ and npm

## Run Locally

### 1) Backend only (skip frontend build)

```bash
mvn clean spring-boot:run -DskipFrontend=true
```

Backend starts at `http://localhost:8080`.

### 2) Frontend in development mode (optional)

```bash
cd frontend
npm install
npm start
```

Frontend runs at `http://localhost:3000` and proxies API requests to `http://localhost:8080`.

### 3) Full package (frontend + backend jar)

```bash
mvn clean package
```

This runs `npm install` + `npm run build`, copies `frontend/build` to `target/classes/static`, and produces the runnable jar in `target/`.

## Default Database and Auth

Current defaults in `src/main/resources/application.properties`:

- H2 file DB: `jdbc:h2:file:~/h2database/stolesdb-v3`
- H2 console: `http://localhost:8080/h2-console`
- JWT expiration: 24 hours
- Allowed CORS origins: `http://localhost:3000`, `http://localhost:5173`

The app seeds default users on first run (from `DataSeeder`):

- `admin` / `admin123` (ADMIN)
- `manager` / `manager123` (MANAGER)
- `javed` / `javed@225001` (ACCOUNT)

Use `POST /api/auth/login` to get a JWT, then pass `Authorization: Bearer <token>` for protected endpoints.

## API Overview

Main endpoint groups (from `Controllers.java`):

- `/api/auth/*`
- `/api/users/*`
- `/api/dashboard`
- `/api/stock/*`
- `/api/workers/*`
- `/api/work-types/*`
- `/api/dispatches/*`
- `/api/payments/*`

For ready-to-use requests, import the files in `postman/`.

## Switch to MySQL (Optional)

In `src/main/resources/application.properties`, comment H2 properties and enable the provided MySQL properties (`spring.datasource.*` and matching dialect), then create your MySQL database before startup.

## Notes

- `ddl-auto=update` is enabled for local development convenience.
- Change `app.jwt.secret` before any production deployment.
