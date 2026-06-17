# BFHL API - DY Patil Campus Hiring (June 2026)

REST API built with Spring Boot for the Bajaj Finserv Health qualifier assessment.

## Tech Stack
- Java 17 + Spring Boot 3.2
- Maven
- JUnit 5 + Mockito

## Running Locally

```bash
mvn clean package -DskipTests
java -jar target/bfhl-api-1.0.0.jar
```

API will start on port 8080.

## Endpoints

### POST /bfhl
Processes a mixed data array and returns categorized analytics.

**Headers:**
- `X-Request-Id: REQ-1001` (optional, auto-generated if absent)

**Request:**
```json
{
  "data": ["A", "1", "22", "$", "B", "7"]
}
```

### GET /bfhl/health
Health check endpoint.

## Running Tests

```bash
mvn test
```

## Deployment (Render)

1. Push to GitHub
2. Connect repo in Render dashboard
3. Set Build Command: `mvn clean package -DskipTests`
4. Set Start Command: `java -jar target/bfhl-api-1.0.0.jar`
5. Deploy

## Architecture

```
controller/   → BfhlController.java      (REST layer)
service/      → BfhlService.java         (interface)
              → BfhlServiceImpl.java     (business logic)
dto/          → BfhlRequest.java
              → BfhlResponse.java
exception/    → GlobalExceptionHandler.java
```
