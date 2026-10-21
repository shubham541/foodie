package com.bits.foodie;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrderFlowApiTest extends ApiTestSupport {

    @Test
    void seededMenuMatchesTheHouseOfShukitaPriceList() throws Exception {
        Response r = call("GET", "/api/v1/restaurants/1/menu", null, null);
        assertThat(r.status()).isEqualTo(200);
        int total = 0;
        for (JsonNode c : r.body().get("categories")) total += c.get("items").size();
        assertThat(total).isEqualTo(52);
        assertThat(menuItem("Paneer Paratha").get("price").decimalValue()).isEqualByComparingTo("199");
        assertThat(menuItem("Egg Fried Maggi").get("veg").asBoolean()).isFalse();
        assertThat(menuItem("No-Skip Poha").get("imageUrl").asText()).isEqualTo("/images/menu/poha.jpg");
    }

    @Test
    void ordersArePricedByTheServerWithGst() throws Exception {
        String customer = registerCustomer();
        JsonNode paneer = menuItem("Paneer Paratha");
        JsonNode maggi = menuItem("Egg Fried Maggi");
        // duplicate lines are merged: 3 x 199 + 1 x 169 = 766, GST 5% = 38.30
        Map<String, Object> body = Map.of("restaurantId", 1,
                "items", List.of(Map.of("menuItemId", paneer.get("id").asLong(), "quantity", 2),
                        Map.of("menuItemId", maggi.get("id").asLong(), "quantity", 1),
                        Map.of("menuItemId", paneer.get("id").asLong(), "quantity", 1)),
                "contactPhone", "9876543210", "paymentMethod", "UPI");
        Response r = call("POST", "/api/v1/orders", customer, body);
        assertThat(r.status()).isEqualTo(201);
        assertThat(r.body().get("items")).hasSize(2);
        assertThat(r.body().get("subtotal").decimalValue()).isEqualByComparingTo(new BigDecimal("766.00"));
        assertThat(r.body().get("taxAmount").decimalValue()).isEqualByComparingTo(new BigDecimal("38.30"));
        assertThat(r.body().get("total").decimalValue()).isEqualByComparingTo(new BigDecimal("804.30"));
        assertThat(r.body().get("status").asText()).isEqualTo("PLACED");
    }

    @Test
    void invalidOrdersAreRejected() throws Exception {
        String customer = registerCustomer();
        long id = menuItem("Paneer Paratha").get("id").asLong();

        assertThat(call("POST", "/api/v1/orders", customer, Map.of("restaurantId", 1, "items", List.of(), "contactPhone", "9876543210",
                "paymentMethod", "CASH")).status()).isEqualTo(400);
        assertThat(call("POST", "/api/v1/orders", customer, orderBody(id, 0)).status()).isEqualTo(400);
        assertThat(call("POST", "/api/v1/orders", customer, orderBody(id, 21)).status()).isEqualTo(400);
        assertThat(call("POST", "/api/v1/orders", customer, orderBody(999_999, 1)).status()).isEqualTo(409);

        Map<String, Object> badPhone = new java.util.HashMap<>(orderBody(id, 1));
        badPhone.put("contactPhone", "123");
        Response r = call("POST", "/api/v1/orders", customer, badPhone);
        assertThat(r.status()).isEqualTo(400);
        assertThat(r.body().get("fieldErrors").has("contactPhone")).isTrue();
    }

    @Test
    void fullLifecycleFromPlacementToPickup() throws Exception {
        String customer = registerCustomer();
        String owner = login(OWNER_EMAIL, OWNER_PASSWORD);
        long itemId = menuItem("Butter Paratha").get("id").asLong();

        long orderId = call("POST", "/api/v1/orders", customer, orderBody(itemId, 2)).body().get("id").asLong();
        String path = "/api/v1/orders/" + orderId;

        // the restaurant sees it; skipping steps is refused
        assertThat(call("GET", "/api/v1/restaurant-admin/orders", owner, null).body().findValues("id")).extracting(JsonNode::asLong).contains(orderId);
        assertThat(call("PATCH", path + "/status", owner, Map.of("status", "READY_FOR_PICKUP")).status()).isEqualTo(409);
        assertThat(call("PATCH", path + "/status", owner, Map.of("status", "COLLECTED")).status()).isEqualTo(409);
        assertThat(call("PATCH", path + "/status", customer, Map.of("status", "ACCEPTED")).status()).isEqualTo(409);

        for (String s : List.of("ACCEPTED", "PREPARING", "READY_FOR_PICKUP")) {
            Response r = call("PATCH", path + "/status", owner, Map.of("status", s));
            assertThat(r.status()).as(s).isEqualTo(200);
            assertThat(r.body().get("status").asText()).isEqualTo(s);
        }
        // too late for the customer to cancel
        assertThat(call("POST", path + "/cancel", customer, Map.of()).status()).isEqualTo(409);

        // the customer cannot mark their own order collected; the restaurant hands it over
        assertThat(call("PATCH", path + "/status", customer, Map.of("status", "COLLECTED")).status()).isEqualTo(409);
        Response collected = call("PATCH", path + "/status", owner, Map.of("status", "COLLECTED"));
        assertThat(collected.status()).isEqualTo(200);
        assertThat(collected.body().get("status").asText()).isEqualTo("COLLECTED");
        assertThat(collected.body().has("collectedAt")).isTrue();

        // terminal: nothing can change it any more
        assertThat(call("POST", path + "/cancel", customer, Map.of()).status()).isEqualTo(409);
        assertThat(call("PATCH", path + "/status", owner, Map.of("status", "PREPARING")).status()).isEqualTo(409);
    }

    @Test
    void customerCanCancelOnlyBeforeAcceptanceAndOnlyOwnOrders() throws Exception {
        String customer = registerCustomer();
        String stranger = registerCustomer();
        long itemId = menuItem("Plain Roti").get("id").asLong();
        long orderId = call("POST", "/api/v1/orders", customer, orderBody(itemId, 1)).body().get("id").asLong();

        assertThat(call("GET", "/api/v1/orders/" + orderId, stranger, null).status()).isEqualTo(404);
        assertThat(call("POST", "/api/v1/orders/" + orderId + "/cancel", stranger, Map.of()).status()).isEqualTo(404);

        Response r = call("POST", "/api/v1/orders/" + orderId + "/cancel", customer, Map.of("reason", "changed my mind"));
        assertThat(r.status()).isEqualTo(200);
        assertThat(r.body().get("status").asText()).isEqualTo("CANCELLED");
        assertThat(r.body().get("cancelReason").asText()).isEqualTo("changed my mind");
    }

    @Test
    void restaurantCanPauseOrdersAndMarkDishesSoldOut() throws Exception {
        String customer = registerCustomer();
        String owner = login(OWNER_EMAIL, OWNER_PASSWORD);
        JsonNode dish = menuItem("Ghee Roti");
        long id = dish.get("id").asLong();

        assertThat(call("PATCH", "/api/v1/restaurant-admin/menu/" + id, owner, Map.of("available", false)).status()).isEqualTo(200);
        assertThat(call("POST", "/api/v1/orders", customer, orderBody(id, 1)).status()).isEqualTo(409);
        assertThat(call("PATCH", "/api/v1/restaurant-admin/menu/" + id, owner, Map.of("available", true)).status()).isEqualTo(200);
        assertThat(call("PATCH", "/api/v1/restaurant-admin/menu/" + id, owner, Map.of("price", -5)).status()).isEqualTo(400);

        assertThat(call("PATCH", "/api/v1/restaurant-admin/settings", owner, Map.of("acceptingOrders", false)).status()).isEqualTo(200);
        try {
            assertThat(call("POST", "/api/v1/orders", customer, orderBody(id, 1)).status()).isEqualTo(409);
        } finally {
            call("PATCH", "/api/v1/restaurant-admin/settings", owner, Map.of("acceptingOrders", true));
        }
        assertThat(call("POST", "/api/v1/orders", customer, orderBody(id, 1)).status()).isEqualTo(201);
    }

    @Test
    void ownerRunsTheWholeBusiness() throws Exception {
        String customer = registerCustomer();
        String owner = login(OWNER_EMAIL, OWNER_PASSWORD);
        long orderId = call("POST", "/api/v1/orders", customer, orderBody(menuItem("No-Skip Poha").get("id").asLong(), 1)).body().get("id").asLong();

        Response summary = call("GET", "/api/v1/restaurant-admin/summary", owner, null);
        assertThat(summary.status()).isEqualTo(200);
        assertThat(summary.body().get("totalOrders").asLong()).isGreaterThanOrEqualTo(1);
        Response report = call("GET", "/api/v1/restaurant-admin/reports", owner, null);
        assertThat(report.status()).isEqualTo(200);
        assertThat(report.body().has("averageOrderValue")).isTrue();
        assertThat(call("GET", "/api/v1/restaurant-admin/orders?filter=ALL", owner, null).body().size()).isGreaterThanOrEqualTo(1);

        // the owner may cancel even once the food is ready (a customer could not)
        for (String s : List.of("ACCEPTED", "PREPARING", "READY_FOR_PICKUP")) {
            assertThat(call("PATCH", "/api/v1/orders/" + orderId + "/status", owner, Map.of("status", s)).status()).isEqualTo(200);
        }
        Response cancelled = call("POST", "/api/v1/orders/" + orderId + "/cancel", owner, Map.of());
        assertThat(cancelled.status()).isEqualTo(200);
        assertThat(cancelled.body().get("cancelReason").asText()).isEqualTo("Cancelled by restaurant");

        // customers can be listed and deactivated, but only customers
        Response customers = call("GET", "/api/v1/restaurant-admin/customers", owner, null);
        assertThat(customers.status()).isEqualTo(200);
        assertThat(customers.body().findValuesAsText("role")).containsOnly("CUSTOMER");
        long ownerId = call("GET", "/api/v1/auth/me", owner, null).body().get("id").asLong();
        assertThat(call("PATCH", "/api/v1/restaurant-admin/customers/" + ownerId + "/active", owner, Map.of("active", false)).status()).isEqualTo(400);
    }

    @Test
    void deliveryIsGone() throws Exception {
        String customer = registerCustomer();
        assertThat(call("GET", "/api/v1/delivery/available", customer, null).status()).isIn(401, 403, 404);
        assertThat(call("POST", "/api/v1/customers/me/addresses", customer, Map.of("line1", "x")).status()).isIn(404, 405);
        Response order = call("POST", "/api/v1/orders", customer, orderBody(menuItem("Plain Paratha").get("id").asLong(), 1));
        assertThat(order.body().has("deliveryAddress")).isFalse();
        assertThat(order.body().has("deliveryFee")).isFalse();
        // the separate admin and rider roles no longer exist: no such accounts, endpoints or pages
        assertThat(call("POST", "/api/v1/auth/login", null, Map.of("email", "admin@foodie.local", "password", "Admin@12345")).status()).isEqualTo(401);
        assertThat(call("GET", "/api/v1/admin/summary", login(OWNER_EMAIL, OWNER_PASSWORD), null).status()).isIn(403, 404);
        for (String page : List.of("/dashboard/delivery", "/dashboard/admin")) {
            assertThat(mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(page))
                    .andReturn().getResponse().getStatus()).as(page).isEqualTo(404);
        }
    }

    @Test
    void pagesAndStaticAssetsAreServed() throws Exception {
        for (String p : List.of("/", "/login", "/register", "/orders", "/credits", "/dashboard/restaurant",
                "/images/brand/logo.jpg", "/images/menu/poha.jpg")) {
            assertThat(mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(p)).andReturn()
                    .getResponse().getStatus()).as(p).isEqualTo(200);
        }
    }
}
