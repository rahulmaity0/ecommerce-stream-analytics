# E-Commerce Stream Analytics Pipeline

[![CI](https://github.com/rahulmaity0/ecommerce-stream-analytics/actions/workflows/ci.yml/badge.svg)](https://github.com/rahulmaity0/ecommerce-stream-analytics/actions/workflows/ci.yml)

This project is a resume-friendly real-time analytics pipeline built with Spring Boot, Kafka, PostgreSQL, and Metabase.

## What it does

- `producer-service` generates random e-commerce order events and publishes them to Kafka.
- `consumer-service` consumes the events, stores them in PostgreSQL, and exposes simple analytics APIs.
- Metabase connects directly to PostgreSQL so you can build dashboards for revenue, top products, and regional trends.

## Architecture

```text
Producer Service -> Kafka Topic -> Consumer Service -> PostgreSQL -> Metabase
```

## Tech stack

- Java 17
- Spring Boot 3
- Spring Kafka
- PostgreSQL
- Docker Compose
- Metabase

## Run the project

### 1. Start the full stack

```powershell
docker compose up -d
```

This starts:

- Kafka on `localhost:9092`
- PostgreSQL on `localhost:5433`
- Producer service on `http://localhost:8081`
- Consumer service on `http://localhost:8082`
- Metabase on `http://localhost:3000`

If Docker returns an error about `dockerDesktopLinuxEngine`, start Docker Desktop first and wait until the engine is running, then run the command again.

### 2. Generate sample traffic manually

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/orders/generate" -ContentType "application/json" -Body '{"count":50}'
```

The producer also auto-generates a small batch every 15 seconds by default.

### 3. Optional: run services manually from your host

This project also supports host-side `spring-boot:run`, but on some Windows + Docker Desktop setups the host-to-Docker PostgreSQL auth path is unreliable.

If you still want to run locally:

```powershell
.\mvnw.cmd -pl producer-service spring-boot:run
.\mvnw.cmd -pl consumer-service spring-boot:run
```

## Useful endpoints

- Producer health: `http://localhost:8081/actuator/health`
- Consumer health: `http://localhost:8082/actuator/health`
- Dashboard snapshot: `http://localhost:8082/api/analytics/dashboard`
- Recent orders: `http://localhost:8082/api/analytics/orders/recent`

## Metabase setup

When Metabase opens for the first time:

1. Create an admin account.
2. Add a PostgreSQL database.
3. Use these connection values:
   - Host: `host.docker.internal`
   - Port: `5433`
   - Database: `analytics`
   - Username: `analytics_user`
   - Password: `analytics_pass`

Use these tables and views:

- `order_events`
- `product_sales_summary`
- `regional_sales_summary`
- `daily_sales_summary`

## Resume bullets

- Built a real-time e-commerce analytics pipeline using Spring Boot, Apache Kafka, PostgreSQL, and Metabase to process and visualize streaming order events.
- Designed an event-driven architecture with producer and consumer microservices, enabling asynchronous ingestion and near real-time business reporting.
- Generated mock order traffic, persisted analytics-ready data, and exposed dashboard APIs for top products, regional revenue, and daily sales trends.

## Interview explanation

`I built two Spring Boot services. One produced random e-commerce order events into Kafka. Another consumed those events, stored them in PostgreSQL, and Metabase read that data to show sales dashboards. I chose Kafka to decouple event generation from analytics processing and make the pipeline scalable.`

## Interview questions to prepare

- Why did you use Kafka instead of direct service-to-service calls?
- What is the role of the consumer group?
- Why did you use PostgreSQL along with Metabase?
- What happens if the consumer service is temporarily down?
- How would you handle duplicate events?
- How would you scale this pipeline?
- How would you move this to AWS?
- How would you track failed events or retries?

## Strong answers in one line

- Kafka decouples event producers and consumers and supports asynchronous scaling.
- Consumer groups let multiple consumer instances share message processing work.
- PostgreSQL stores structured analytics data and Metabase sits on top for reporting.
- Duplicate events are prevented here with `order_id` as the primary key.
- On AWS, Kafka could move to MSK, PostgreSQL to RDS, and services to ECS or EC2.
