package com.bits.foodie.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * One-off clean-up for databases created while the app still supported home delivery and a separate admin.
 * It removes delivery partner and admin accounts, the address book and the delivery columns, and maps old order
 * statuses onto the pickup flow. Every step is guarded by a schema check, so on a fresh or already-migrated
 * database it does nothing. Runs before the data seeder.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LegacyDeliveryCleanup implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LegacyDeliveryCleanup.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbc;

    public LegacyDeliveryCleanup(DataSource dataSource, JdbcTemplate jdbc) {
        this.dataSource = dataSource;
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) throws SQLException {
        try (Connection c = dataSource.getConnection()) {
            DatabaseMetaData md = c.getMetaData();
            String catalog = c.getCatalog();
            String schema = c.getSchema();
            boolean mysql = md.getDatabaseProductName().toLowerCase().contains("mysql");

            if (!columns(md, catalog, schema, "addresses").isEmpty()) {
                jdbc.execute("drop table addresses");
                log.info("Dropped the addresses table (pickup orders need no delivery address)");
            }

            Map<String, String> orderCols = columns(md, catalog, schema, "orders");
            if (orderCols.containsKey("DELIVERY_PARTNER_ID") || orderCols.containsKey("DELIVERY_FEE")
                    || orderCols.containsKey("DELIVERY_ADDRESS") || orderCols.containsKey("DELIVERED_AT")) {
                // Hibernate maps enums to a native ENUM type that "update" never widens, so make it plain text first.
                jdbc.execute(mysql ? "alter table orders modify column status varchar(20) not null"
                        : "alter table orders alter column status set data type varchar(20)");
                if (orderCols.containsKey("DELIVERY_PARTNER_ID")) {
                    dropForeignKeys(md, catalog, schema, mysql, "DELIVERY_PARTNER_ID");
                }
                int moved = jdbc.update("update orders set status = 'COLLECTED' where status = 'DELIVERED'")
                        + jdbc.update("update orders set status = 'READY_FOR_PICKUP' where status = 'OUT_FOR_DELIVERY'");
                if (moved > 0) log.info("Mapped {} old delivery order(s) onto the pickup statuses", moved);
                if (orderCols.containsKey("DELIVERED_AT")) {
                    jdbc.update("update orders set collected_at = delivered_at where collected_at is null and delivered_at is not null");
                }
                for (String col : new String[]{"delivery_partner_id", "delivery_fee", "delivery_address", "delivered_at"}) {
                    dropColumn("orders", col, orderCols);
                }
            }

            Map<String, String> userCols = columns(md, catalog, schema, "users");
            if (userCols.containsKey("VEHICLE_TYPE") || userCols.containsKey("AVAILABLE")) {
                int removed = jdbc.update("delete from users where role = 'DELIVERY_PARTNER'");
                if (removed > 0) log.info("Removed {} delivery partner account(s)", removed);
                dropColumn("users", "vehicle_type", userCols);
                dropColumn("users", "available", userCols);
            }

            // The separate admin role is gone (the restaurant owner manages everything). On a fresh database the
            // role column is an ENUM without ADMIN, so the comparison itself is rejected and there is nothing to delete.
            if (!userCols.isEmpty()) {
                try {
                    int admins = jdbc.update("delete from users where role = 'ADMIN'");
                    if (admins > 0) log.info("Removed {} old admin account(s); the restaurant owner now manages everything", admins);
                } catch (DataAccessException e) {
                    log.debug("No ADMIN role in this schema, nothing to remove");
                }
            }
        }
    }

    private void dropColumn(String table, String column, Map<String, String> existing) {
        if (existing.containsKey(column.toUpperCase())) {
            jdbc.execute("alter table " + table + " drop column " + column);
            log.info("Dropped {}.{}", table, column);
        }
    }

    private void dropForeignKeys(DatabaseMetaData md, String catalog, String schema, boolean mysql, String column) throws SQLException {
        Set<String> names = new HashSet<>();
        for (String t : new String[]{"orders", "ORDERS"}) {
            try (ResultSet rs = md.getImportedKeys(catalog, schema, t)) {
                while (rs.next()) {
                    if (column.equalsIgnoreCase(rs.getString("FKCOLUMN_NAME")) && rs.getString("FK_NAME") != null) {
                        names.add(rs.getString("FK_NAME"));
                    }
                }
            }
        }
        for (String fk : names) {
            jdbc.execute("alter table orders" + (mysql ? " drop foreign key " : " drop constraint ") + fk);
        }
    }

    /** Upper-cased column name to type name (matched case-insensitively); empty when the table does not exist. */
    private static Map<String, String> columns(DatabaseMetaData md, String catalog, String schema, String table) throws SQLException {
        Map<String, String> cols = new HashMap<>();
        for (String t : new String[]{table, table.toUpperCase()}) {
            try (ResultSet rs = md.getColumns(catalog, schema, t, null)) {
                while (rs.next()) cols.put(rs.getString("COLUMN_NAME").toUpperCase(), rs.getString("TYPE_NAME"));
            }
        }
        return cols;
    }
}
