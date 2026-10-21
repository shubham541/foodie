package com.bits.foodie;

import com.bits.foodie.config.LegacyDeliveryCleanup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static org.assertj.core.api.Assertions.assertThat;

/** Builds a database shaped like the old home-delivery version and checks the one-off migration. */
class LegacyDeliveryCleanupTest {

    @Test
    void migratesAnOldDeliveryDatabaseToPickup() throws Exception {
        DriverManagerDataSource ds = new DriverManagerDataSource("jdbc:h2:mem:legacy-cleanup;DB_CLOSE_DELAY=-1", "sa", "");
        JdbcTemplate jdbc = new JdbcTemplate(ds);
        // Hibernate created enums as native ENUM columns, which "ddl-auto=update" never widens
        jdbc.execute("create table users (id bigint primary key, role enum('CUSTOMER','RESTAURANT','DELIVERY_PARTNER','ADMIN') not null,"
                + " vehicle_type varchar(30), available boolean not null)");
        jdbc.execute("create table addresses (id bigint primary key, user_id bigint not null, foreign key (user_id) references users(id))");
        jdbc.execute("create table orders (id bigint primary key,"
                + " status enum('PLACED','ACCEPTED','PREPARING','READY_FOR_PICKUP','OUT_FOR_DELIVERY','DELIVERED','CANCELLED') not null, collected_at timestamp, delivered_at timestamp,"
                + " delivery_fee decimal(10,2) not null, delivery_address varchar(500) not null, delivery_partner_id bigint,"
                + " foreign key (delivery_partner_id) references users(id))");
        jdbc.update("insert into users values (1, 'CUSTOMER', null, false)");
        jdbc.update("insert into users values (2, 'DELIVERY_PARTNER', 'BIKE', true)");
        jdbc.update("insert into users values (3, 'ADMIN', null, false)");
        jdbc.update("insert into users values (4, 'RESTAURANT', null, false)");
        jdbc.update("insert into addresses values (1, 1)");
        jdbc.update("insert into orders values (1, 'DELIVERED', null, current_timestamp, 0, 'x', 2)");
        jdbc.update("insert into orders values (2, 'OUT_FOR_DELIVERY', null, null, 0, 'x', 2)");
        jdbc.update("insert into orders values (3, 'PLACED', null, null, 0, 'x', null)");

        new LegacyDeliveryCleanup(ds, jdbc).run(new DefaultApplicationArguments());

        // only the customer and the restaurant owner survive: rider and admin accounts are removed
        assertThat(jdbc.queryForList("select role from users order by id", String.class)).containsExactly("CUSTOMER", "RESTAURANT");
        assertThat(jdbc.queryForObject("select status from orders where id = 1", String.class)).isEqualTo("COLLECTED");
        assertThat(jdbc.queryForObject("select status from orders where id = 2", String.class)).isEqualTo("READY_FOR_PICKUP");
        assertThat(jdbc.queryForObject("select status from orders where id = 3", String.class)).isEqualTo("PLACED");
        assertThat(jdbc.queryForObject("select collected_at from orders where id = 1", Object.class)).isNotNull();
        assertThat(jdbc.queryForObject("select count(*) from information_schema.columns where table_name = 'ORDERS' "
                + "and column_name in ('DELIVERY_FEE','DELIVERY_ADDRESS','DELIVERY_PARTNER_ID','DELIVERED_AT')", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("select count(*) from information_schema.tables where table_name = 'ADDRESSES'", Integer.class)).isZero();

        // running it again on the migrated database is a no-op
        new LegacyDeliveryCleanup(ds, jdbc).run(new DefaultApplicationArguments());
        assertThat(jdbc.queryForList("select id from orders")).hasSize(3);
    }
}
