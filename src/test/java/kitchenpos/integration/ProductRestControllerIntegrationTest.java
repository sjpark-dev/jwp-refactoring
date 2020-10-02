package kitchenpos.integration;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ProductRestControllerIntegrationTest extends IntegrationTest {

    @DisplayName("상품 생성")
    @Test
    void create() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "강정치킨");
        data.put("price", "17000");

        // @formatter:off
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            body(data).
        when().
            post("/api/products").
        then().
            assertThat().
            statusCode(HttpStatus.CREATED.value()).
            header("Location", containsString("/api/products/")).
            body("name", equalTo("강정치킨")).
            body("price", any(Float.class));
        // @formatter:on
    }

    @DisplayName("상품 전체 조회")
    @Test
    void list() {
        // @formatter:off
        given().
        when().
            get("/api/products").
        then().
            assertThat().
            statusCode(HttpStatus.OK.value()).
            body("$", hasSize(greaterThan(0)));
        // @formatter:on
    }
}
