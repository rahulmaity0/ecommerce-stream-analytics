# Interview Guide

## 30-second explanation

I built a real-time e-commerce analytics pipeline using two Spring Boot services. The producer generates random order events and publishes them to Kafka. The consumer listens to Kafka, stores analytics-ready order data in PostgreSQL, and Metabase reads from PostgreSQL to create dashboards like top products, revenue by region, and daily sales trends.

## 60-second explanation

The goal of the project was to simulate how an event-driven commerce platform sends transactional data into an analytics pipeline. I separated the system into a producer service and a consumer service so the event source is decoupled from analytics processing. Kafka acts as the buffer and transport layer. The consumer persists cleaned data into PostgreSQL, and Metabase is used on top of the database for visualization. This design is simple enough for a demo but maps well to real production patterns.

## Architecture

- Producer service: creates random order events and publishes them to the `order-events` Kafka topic.
- Kafka: buffers and delivers the event stream asynchronously.
- Consumer service: reads from Kafka and saves events to PostgreSQL.
- PostgreSQL: stores raw event records and exposes SQL views for analytics.
- Metabase: reads tables and views to create dashboards.

## What each technology is doing

- Spring Boot: fast way to build the producer and consumer services.
- Kafka: event streaming and decoupling between services.
- PostgreSQL: structured storage for reporting queries.
- Metabase: BI layer for dashboards.
- Docker Compose: easy local setup for infrastructure.

## Most likely interview questions

### Why did you use Kafka?

Kafka helps decouple the event-producing service from the analytics service. The producer does not need to wait for the consumer, and consumers can scale independently.

### Why not call the consumer service directly?

Direct HTTP calls tightly couple both services. Kafka gives asynchronous processing, buffering, and easier scaling.

### What is the benefit of a consumer group?

A consumer group allows multiple instances of the same consumer service to share partitions and scale message processing horizontally.

### Why store the data in PostgreSQL if Kafka already has the events?

Kafka is a streaming backbone, not the main analytics database here. PostgreSQL is better for SQL queries, views, and dashboard tools like Metabase.

### How is duplicate processing handled?

The project uses `order_id` as the primary key in PostgreSQL, so duplicate inserts are ignored with `ON CONFLICT DO NOTHING`.

### What happens if the consumer is down?

Kafka retains the messages, so when the consumer comes back it can continue reading from the topic based on offsets.

### What happens if Kafka is down?

The producer will fail to publish events until Kafka is available again. In production I would add retries, monitoring, and possibly a dead-letter strategy.

### How would you scale this project?

- Increase Kafka partitions
- Run multiple consumer instances in the same consumer group
- Move PostgreSQL to a managed service with better sizing
- Add caching or pre-aggregated tables for heavier dashboards

### How would you move this to AWS?

- Kafka to Amazon MSK
- PostgreSQL to Amazon RDS
- Spring Boot apps to ECS, EKS, or EC2
- Metabase on EC2 or replace with QuickSight
- Monitoring with CloudWatch

### Why did you use views?

Views make reporting cleaner because Metabase can query already-aggregated business metrics instead of repeating heavy aggregation logic every time.

### Why random data generation?

It lets the pipeline behave like a live production system without needing a real commerce frontend or external data source.

## Questions they may ask from the code

### How are events generated?

The producer creates random orders using a fixed product catalog, random regions, quantities, payment methods, and discounts.

### How are messages serialized?

The producer converts the event object to JSON using Jackson and sends it as a string message to Kafka.

### How does the consumer parse the event?

The consumer receives the Kafka payload as a string and uses Jackson to deserialize it back into an `OrderEvent`.

### How is analytics exposed?

The consumer provides REST endpoints for dashboard snapshots and recent orders, and the same data is also available directly in PostgreSQL for Metabase.

## Honest limitations to admit confidently

- It is a demo pipeline with generated data rather than real user traffic.
- It uses a single Kafka broker locally, not a production cluster.
- It does not yet include retries, dead-letter queues, or schema registry.
- It focuses on data flow and analytics clarity over production hardening.

## Smart improvements you can mention

- Add dead-letter topics for failed events
- Add schema versioning with Avro or Protobuf
- Use Redis for caching hot dashboard queries
- Deploy to AWS with MSK and RDS
- Add Prometheus and Grafana for monitoring

## Memory version

Remember this sentence:

`Producer makes order events, Kafka transports them, consumer saves them, PostgreSQL stores them, Metabase visualizes them.`
