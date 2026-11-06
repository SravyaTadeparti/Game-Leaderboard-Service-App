# Game Leaderboard Service

A Spring Boot leaderboard that persists player totals in PostgreSQL and uses a Redis sorted set for fast, ranked reads.

## Run locally

```powershell
docker compose up --build
```

- API: <http://localhost:8080>
- Health: <http://localhost:8080/actuator/health>
- Prometheus metrics: <http://localhost:8080/actuator/prometheus>
- Prometheus UI: <http://localhost:9090>

## API

Submit points:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/api/scores `
  -ContentType "application/json" `
  -Body '{"playerName":"Ada","score":120}'
```

Fetch the top 10:

```powershell
Invoke-RestMethod http://localhost:8080/api/leaderboard/top/10
```

## Architecture

```text
Client -> Spring Boot REST API -> PostgreSQL (durable player totals)
                             \-> Redis sorted set (hot ranking index)
```

Redis updates are O(log n). PostgreSQL remains the durable source of player totals.

## Test and load test

```powershell
mvn test
k6 run k6/load-test.js
```

## Observability

Spring Boot's HTTP server metrics, including `http_server_requests_seconds`, are exposed through Micrometer and scraped by Prometheus.

## Load-test results

Measured with k6 against the Docker Compose stack, a 60-second run with up to 20 virtual users, and the `POST /api/scores` plus `GET /api/leaderboard/top/10` workload:

**Pending real run:** Docker Desktop is installed but its engine could not start in this environment without elevated privileges, so verified request, throughput, latency, and failure numbers could not be recorded without fabricating data.
