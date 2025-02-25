package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import io.qameta.allure.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@Epic("API Testing")
@Feature("GET Requests")
public class GetRequestTest {

    static {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Severity(SeverityLevel.CRITICAL)
    @Story("Valid GET requests with existing post IDs")
    @Description("Verify that the API returns correct responses for valid post IDs (1-5)")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    public void testValidGetRequest(int postId) {
        Allure.step("Sending GET request to /posts/" + postId);

        Response response = given()
                .header("Accept", "application/json")
                .when()
                .get("/posts/" + postId)
                .then()
                .statusCode(200)
                .header("Content-Type", equalTo("application/json; charset=utf-8"))
                .body("id", equalTo(postId))
                .body("userId", notNullValue())
                .body("title", notNullValue())
                .body("body", notNullValue())
                .body(matchesJsonSchemaInClasspath("post-schema.json"))
                .time(lessThan(2000L))
                .log().all()
                .extract().response();

        Allure.step("Response: " + response.asString());
    }

    @Severity(SeverityLevel.NORMAL)
    @Story("Invalid GET requests with non-existent post IDs")
    @Description("Verify that the API returns 404 Not Found for invalid post IDs (0, 101, 9999, -1)")
    @ParameterizedTest
    @ValueSource(ints = {0, 101, 9999, -1})
    public void testInvalidGetRequest(int postId) {
        Allure.step("Sending invalid GET request to /posts/" + postId);

        given()
                .header("Accept", "application/json")
                .when()
                .get("/posts/" + postId)
                .then()
                .statusCode(404)
                .log().all();

        Allure.step("Verified 404 response for post ID: " + postId);
    }

    @Severity(SeverityLevel.MINOR)
    @Story("Response time validation")
    @Description("Ensure API response time is under 1.5 seconds for valid post IDs")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    public void testResponseTime(int postId) {
        Allure.step("Validating response time for post ID: " + postId);

        given()
                .header("Accept", "application/json")
                .when()
                .get("/posts/" + postId)
                .then()
                .time(lessThan(1500L))
                .log().all();

        Allure.step("Response time validated for post ID: " + postId);
    }
}
