package com.bits.foodie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** Shared helpers for the integration tests: boots the full app on H2 and talks to it through MockMvc. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class ApiTestSupport {

    protected static final String OWNER_EMAIL = "houseofshukita@gmail.com";
    protected static final String OWNER_PASSWORD = "Shukita@12345";

    private static final AtomicLong COUNTER = new AtomicLong(System.nanoTime());

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper mapper;

    protected record Response(int status, JsonNode body) {
    }

    protected Response call(String method, String path, String token, Object body) throws Exception {
        MockHttpServletRequestBuilder req = switch (method) {
            case "GET" -> get(path);
            case "POST" -> post(path);
            case "PATCH" -> patch(path);
            case "DELETE" -> delete(path);
            default -> throw new IllegalArgumentException(method);
        };
        if (token != null) req.header("Authorization", "Bearer " + token);
        if (body != null) req.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body));
        MvcResult result = mvc.perform(req).andReturn();
        String text = result.getResponse().getContentAsString();
        return new Response(result.getResponse().getStatus(), text.isBlank() ? null : mapper.readTree(text));
    }

    protected String login(String email, String password) throws Exception {
        Response r = call("POST", "/api/v1/auth/login", null, Map.of("email", email, "password", password));
        if (r.status() != 200) throw new IllegalStateException("login failed for " + email + ": " + r.status());
        return r.body().get("token").asText();
    }

    protected String uniqueEmail(String prefix) {
        return prefix + COUNTER.incrementAndGet() + "@example.com";
    }

    protected String registerCustomer() throws Exception {
        Response r = call("POST", "/api/v1/auth/register", null, Map.of("name", "Test Customer",
                "email", uniqueEmail("cust"), "phone", "9876543210", "password", "Passw0rd1"));
        if (r.status() != 201) throw new IllegalStateException("register failed: " + r.body());
        return r.body().get("token").asText();
    }

    protected JsonNode menuItem(String name) throws Exception {
        for (JsonNode category : call("GET", "/api/v1/restaurants/1/menu", null, null).body().get("categories")) {
            for (JsonNode item : category.get("items")) {
                if (item.get("name").asText().equals(name)) return item;
            }
        }
        throw new IllegalStateException("No menu item " + name);
    }

    protected Map<String, Object> orderBody(long menuItemId, int quantity) {
        return Map.of("restaurantId", 1, "items", java.util.List.of(Map.of("menuItemId", menuItemId, "quantity", quantity)),
                "contactPhone", "9876543210", "paymentMethod", "CASH");
    }
}
