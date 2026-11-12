# API Health Dashboard

Spring Boot (MVC) + PostgreSQL + Kafka + Grafana + Angular, all in Docker.

## Run
```bash
docker compose up --build
```
| What | URL |
|---|---|
| Dashboard (Angular) | http://localhost:4200 |
| Backend API | http://localhost:8080 |
| Grafana | http://localhost:3000 (admin / admin) |

## Users
| user / password | role | can do |
|---|---|---|
| viewer / viewer123 | VIEWER | see dashboard + filters |
| operator / operator123 | OPERATOR | + open logs, "Check now" per API, Grafana link |
| admin / admin123 | ADMIN | + "Check all now" (also re-reads apis.yml) |

## Add / change an API  (ONE place only)
Edit `config/apis.yml`. No rebuild, no restart: the backend re-reads it every cycle (15s)
and the UI filters/dropdowns update automatically. Remove an entry = it is disabled (history kept).

## Flow
```
config/apis.yml -> ApiConfigService (sync to DB)
Scheduler (15s) -> HealthCheckService (REST/SOAP call) -> Kafka topic api.health.results
Kafka -> CheckResultConsumer -> check_log table + latest status on api_endpoint
Angular (poll 5s) -> Spring controllers -> DB        Grafana -> same Postgres
```

## Backend structure (MVC)
```
controller/  ApiController, AuthController        (HTTP only)
service/     ApiConfigService, HealthCheckService, MonitorService, CheckScheduler,
             CheckResultProducer/Consumer, ApiQueryService   (logic)
repository/  Spring Data JPA interfaces
model/       ApiEndpoint, CheckLog, AppUser (entities), ApiDefinition (yaml shape)
dto/         ApiStatusDto, CheckResultEvent
config/      SecurityConfig, KafkaConfig, DataInitializer
```

## Local dev without Docker for app code
`docker compose up postgres kafka grafana`, then `cd backend && mvn spring-boot:run`
(Kafka advertises `kafka:9092`; for host access add `127.0.0.1 kafka` to your hosts file),
and `cd frontend && npm install && npm start` (proxies /api to :8080).
