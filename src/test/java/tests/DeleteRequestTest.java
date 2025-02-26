package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import io.qameta.allure.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ExtendWith(io.qameta.allure.junit5.AllureJunit5.class)
@Epic("API Testing")
@Feature("DELETE Requests")
public class DeleteRequestTest {

    static {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com"; // Example API
    }

    @Nested
    @DisplayName("Positive Test Cases")
    class PositiveTests {

        @Test
        @DisplayName("Test deleting an existing post")
        public void testDeleteExistingPost() {
            given()
                    .header("Content-Type", "application/json")
                    .when()
                    .delete("/posts/1")
                    .then()
                    .statusCode(200) // JSONPlaceholder returns 200 even for deletes
                    .header("Content-Type", notNullValue())
                    .body(equalTo("{}")) // Empty response body for a delete
                    .log().all();
        }
    }

    @Nested
    @DisplayName("Negative Test Cases")
    class NegativeTests {

        @Test
        @DisplayName("Test deleting a non-existent post (still returns 200)")
        public void testDeleteNonExistentPost() {
            given()
                    .header("Content-Type", "application/json")
                    .when()
                    .delete("/posts/9999") // non-existent post
                    .then()
                    .statusCode(200) // Should be 404, but JSONPlaceholder returns 200
                    .body(equalTo("{}")) // Validate empty JSON body
                    .log().all();
        }
    }
}
