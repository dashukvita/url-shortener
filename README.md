# Url-Shortener 
A simple URL shortener with caching, MongoDB storage, and web UI.

### Getting Started
```bash
mvn clean install
docker-compose up
```
- Run `UrlShortenerApplication` from Intellij.

-----------------
### App

- http://localhost:8080/
  — web UI

### Swagger

- http://localhost:8080/swagger-ui/index.html
  — explore API interactively


-----------------
### Features
- **URL Shortening** — stores mapping in **MongoDB**  
- **Caching** — Redis cache with TTL (30 days) for fast lookups  
- **Web & API** — provides both **REST API** and **web UI** (Thymeleaf + Ajax)  
- **Hash Generation** — SHA-256 + Base62, with collision fallback  
- **URL Validation** — blocks loopback/localhost addresses  
- **Thread-Safe Concurrency** — optimistic handling via `DuplicateKeyException`

### Architecture
- **Client** — Web UI / REST client
- **Controller** — Handles requests and validation
- **Service** — Generates hashes, manages cache and storage
- **Redis** — Fast cache lookup
- **MongoDB** — Source of truth, persistent storage