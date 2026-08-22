package com.rahul.ecommerce.consumer.repository;

import com.rahul.ecommerce.consumer.model.DailySalesSummary;
import com.rahul.ecommerce.consumer.model.OrderEvent;
import com.rahul.ecommerce.consumer.model.OrderEventRow;
import com.rahul.ecommerce.consumer.model.ProductSalesSummary;
import com.rahul.ecommerce.consumer.model.RegionSalesSummary;
import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;

@Repository
public class AnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveOrderEvent(OrderEvent event) {
        jdbcTemplate.update("""
                INSERT INTO order_events (
                    order_id, customer_id, product_id, product_name, category, region, quantity,
                    unit_price, discount_amount, total_amount, payment_method, order_status, event_time
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (order_id) DO NOTHING
                """,
                event.orderId(),
                event.customerId(),
                event.productId(),
                event.productName(),
                event.category(),
                event.region(),
                event.quantity(),
                event.unitPrice(),
                event.discountAmount(),
                event.totalAmount(),
                event.paymentMethod(),
                event.orderStatus(),
                Timestamp.from(event.eventTime() == null ? Instant.now() : event.eventTime())
        );
    }

    public List<OrderEventRow> findRecentOrders() {
        return jdbcTemplate.query("""
                SELECT order_id, product_name, category, region, quantity, total_amount, payment_method, order_status, event_time
                FROM order_events
                ORDER BY event_time DESC
                LIMIT 20
                """,
                (rs, rowNum) -> new OrderEventRow(
                        rs.getString("order_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getString("region"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("total_amount"),
                        rs.getString("payment_method"),
                        rs.getString("order_status"),
                        rs.getTimestamp("event_time").toInstant()
                ));
    }

    public List<ProductSalesSummary> findTopProducts() {
        return jdbcTemplate.query("""
                SELECT product_name, category, total_orders, total_units_sold, revenue
                FROM product_sales_summary
                ORDER BY revenue DESC
                LIMIT 5
                """,
                (rs, rowNum) -> new ProductSalesSummary(
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getLong("total_orders"),
                        rs.getLong("total_units_sold"),
                        rs.getBigDecimal("revenue")
                ));
    }

    public List<RegionSalesSummary> findRegionalPerformance() {
        return jdbcTemplate.query("""
                SELECT region, total_orders, revenue
                FROM regional_sales_summary
                ORDER BY revenue DESC
                """,
                (rs, rowNum) -> new RegionSalesSummary(
                        rs.getString("region"),
                        rs.getLong("total_orders"),
                        rs.getBigDecimal("revenue")
                ));
    }

    public List<DailySalesSummary> findDailyTrend() {
        return jdbcTemplate.query("""
                SELECT order_date, total_orders, total_units_sold, revenue
                FROM daily_sales_summary
                ORDER BY order_date DESC
                LIMIT 7
                """,
                (rs, rowNum) -> new DailySalesSummary(
                        rs.getDate("order_date").toLocalDate(),
                        rs.getLong("total_orders"),
                        rs.getLong("total_units_sold"),
                        rs.getBigDecimal("revenue")
                ));
    }

    public long countOrders() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM order_events", Long.class);
        return count == null ? 0 : count;
    }
}
