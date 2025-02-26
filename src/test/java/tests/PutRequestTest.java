package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import io.qameta.allure.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@ExtendWith(io.qameta.allure.junit5.AllureJunit5.class)
@Epic("API Testing")
@Feature("PUT Requests")
public class PutRequestTest {

    static {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @ParameterizedTest(name = "Test case {index}: userId={0}, id={1}, title={2}, body={3}")
    @CsvSource({
            // Valid updates
            "1, 1, 'Updated Title', 'Updated Body'",
            "2, 2, 'Another Title', 'Another Body'",
            "1, 1, 'A', 'B'"
    })
    public void testValidPutRequests(int userId, int id, String title, String body) {
        String requestBody = String.format("""
            {
                "userId": %d,
                "id": %d,
                "title": "%s",
                "body": "%s"
            }
            """, userId, id, title, body);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .body(requestBody)
                .when()
                .put("/posts/" + id);

        // Log the full response
        response.then().log().all();

        // Assert status code
        response.then().statusCode(200);

        // Validate JSON schema
        response.then().body(matchesJsonSchemaInClasspath("post-schema.json"));

        // Validate response body matches request
        response.then()
                .body("id", equalTo(id))
                .body("userId", equalTo(userId))
                .body("title", equalTo(title))
                .body("body", equalTo(body));

        // Validate headers
        response.then().header("Content-Type", containsString("application/json"));
    }
}
