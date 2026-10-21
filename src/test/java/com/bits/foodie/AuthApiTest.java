package com.bits.foodie;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthApiTest extends ApiTestSupport {

    private Map<String, Object> registration(String email, String password) {
        return Map.of("name", "Asha", "email", email, "phone", "9876543210", "password", password);
    }

    @Test
    void registersAndLogsInCustomer() throws Exception {
        String email = uniqueEmail("asha");
        Response reg = call("POST", "/api/v1/auth/register", null, registration(email, "Passw0rd1"));
        assertThat(reg.status()).isEqualTo(201);
        assertThat(reg.body().get("user").get("role").asText()).isEqualTo("CUSTOMER");
        assertThat(reg.body().get("user").has("passwordHash")).isFalse();

        Response login = call("POST", "/api/v1/auth/login", null, Map.of("email", email.toUpperCase(), "password", "Passw0rd1"));
        assertThat(login.status()).isEqualTo(200);

        Response me = call("GET", "/api/v1/auth/me", login.body().get("token").asText(), null);
        assertThat(me.body().get("email").asText()).isEqualTo(email);
    }

    @Test
    void rejectsWeakPasswordWithFieldError() throws Exception {
        Response r = call("POST", "/api/v1/auth/register", null, registration(uniqueEmail("weak"), "weak"));
        assertThat(r.status()).isEqualTo(400);
        assertThat(r.body().get("fieldErrors").has("password")).isTrue();
    }

    @Test
    void rejectsDuplicateEmailCaseInsensitively() throws Exception {
        String email = uniqueEmail("dup");
        assertThat(call("POST", "/api/v1/auth/register", null, registration(email, "Passw0rd1")).status()).isEqualTo(201);
        assertThat(call("POST", "/api/v1/auth/register", null, registration(email.toUpperCase(), "Passw0rd1")).status()).isEqualTo(409);
    }

    @Test
    void selfRegistrationAlwaysCreatesACustomer() throws Exception {
        for (String role : new String[]{"RESTAURANT", "ADMIN", "DELIVERY_PARTNER"}) {
            Response r = call("POST", "/api/v1/auth/register", null, Map.of("role", role, "name", "Eve", "email", uniqueEmail("eve"),
                    "phone", "9876543210", "password", "Passw0rd1"));
            assertThat(r.status()).as(role).isEqualTo(201);
            assertThat(r.body().get("user").get("role").asText()).as(role).isEqualTo("CUSTOMER");
        }
    }

    @Test
    void wrongPasswordAndUnknownEmailGiveTheSameError() throws Exception {
        String email = uniqueEmail("same");
        call("POST", "/api/v1/auth/register", null, registration(email, "Passw0rd1"));
        Response wrong = call("POST", "/api/v1/auth/login", null, Map.of("email", email, "password", "nope1234"));
        Response unknown = call("POST", "/api/v1/auth/login", null, Map.of("email", uniqueEmail("ghost"), "password", "nope1234"));
        assertThat(wrong.status()).isEqualTo(401);
        assertThat(unknown.status()).isEqualTo(401);
        assertThat(wrong.body().get("message").asText()).isEqualTo(unknown.body().get("message").asText());
    }

    @Test
    void locksAccountAfterRepeatedFailures() throws Exception {
        String email = uniqueEmail("lock");
        call("POST", "/api/v1/auth/register", null, registration(email, "Passw0rd1"));
        for (int i = 0; i < 5; i++) {
            call("POST", "/api/v1/auth/login", null, Map.of("email", email, "password", "bad-password1"));
        }
        Response r = call("POST", "/api/v1/auth/login", null, Map.of("email", email, "password", "Passw0rd1"));
        assertThat(r.status()).isEqualTo(429);
    }

    @Test
    void protectedEndpointsNeedAValidToken() throws Exception {
        assertThat(call("GET", "/api/v1/orders/mine", null, null).status()).isEqualTo(401);
        assertThat(call("GET", "/api/v1/orders/mine", "garbage.token.value", null).status()).isEqualTo(401);
    }

    @Test
    void rolesAreEnforcedOnRoleSpecificApis() throws Exception {
        String customer = registerCustomer();
        for (String path : new String[]{"orders", "summary", "reports", "customers"}) {
            assertThat(call("GET", "/api/v1/restaurant-admin/" + path, customer, null).status()).as(path).isEqualTo(403);
        }

        String owner = login(OWNER_EMAIL, OWNER_PASSWORD);
        assertThat(call("GET", "/api/v1/customers/me", owner, null).status()).isEqualTo(403);
        assertThat(call("POST", "/api/v1/orders", owner, orderBody(menuItem("Plain Roti").get("id").asLong(), 1)).status()).isEqualTo(403);
    }

    @Test
    void deactivatedUserLosesAccessImmediately() throws Exception {
        String email = uniqueEmail("gone");
        call("POST", "/api/v1/auth/register", null, registration(email, "Passw0rd1"));
        String token = login(email, "Passw0rd1");
        String owner = login(OWNER_EMAIL, OWNER_PASSWORD);
        long id = call("GET", "/api/v1/restaurant-admin/customers", owner, null).body().findParents("email").stream()
                .filter(n -> n.get("email").asText().equals(email)).findFirst().orElseThrow().get("id").asLong();

        assertThat(call("PATCH", "/api/v1/restaurant-admin/customers/" + id + "/active", owner, Map.of("active", false)).status()).isEqualTo(200);
        assertThat(call("GET", "/api/v1/orders/mine", token, null).status()).isEqualTo(401);
        assertThat(call("POST", "/api/v1/auth/login", null, Map.of("email", email, "password", "Passw0rd1")).status()).isEqualTo(403);
    }
}
