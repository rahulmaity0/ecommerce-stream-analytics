CREATE TABLE IF NOT EXISTS order_events (
    order_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    category VARCHAR(80) NOT NULL,
    region VARCHAR(30) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    discount_amount NUMERIC(12, 2) NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    order_status VARCHAR(30) NOT NULL,
    event_time TIMESTAMP NOT NULL
);

DROP VIEW IF EXISTS product_sales_summary;
CREATE VIEW product_sales_summary AS
SELECT
    product_name,
    category,
    COUNT(*) AS total_orders,
    SUM(quantity) AS total_units_sold,
    SUM(total_amount) AS revenue
FROM order_events
GROUP BY product_name, category;

DROP VIEW IF EXISTS regional_sales_summary;
CREATE VIEW regional_sales_summary AS
SELECT
    region,
    COUNT(*) AS total_orders,
    SUM(total_amount) AS revenue
FROM order_events
GROUP BY region;

DROP VIEW IF EXISTS daily_sales_summary;
CREATE VIEW daily_sales_summary AS
SELECT
    CAST(event_time AS DATE) AS order_date,
    COUNT(*) AS total_orders,
    SUM(quantity) AS total_units_sold,
    SUM(total_amount) AS revenue
FROM order_events
GROUP BY CAST(event_time AS DATE);
