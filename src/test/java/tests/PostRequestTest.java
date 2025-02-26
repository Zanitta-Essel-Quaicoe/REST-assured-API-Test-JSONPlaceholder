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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(io.qameta.allure.junit5.AllureJunit5.class)
@Epic("API Testing")
@Feature("POST Requests")
public class PostRequestTest {

    static {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @ParameterizedTest
    @CsvSource({
            "1, 'Sample Title 1', 'Sample Body 1'",
            "2, 'Sample Title 2', 'Sample Body 2'",
            "3, 'Sample Title 3', 'Sample Body 3'"
    })
    public void testParameterizedPostRequest(int userId, String title, String body) {
        String requestBody = String.format("{\"userId\": %d, \"title\": \"%s\", \"body\": \"%s\"}", userId, title, body);

        Response response = given()
                .header("Content-Type", "application/json")
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .header("Content-Type", equalTo("application/json; charset=utf-8"))
                .body("id", notNullValue())
                .body("userId", equalTo(userId))
                .body("title", equalTo(title))
                .body("body", equalTo(body))
                .body(matchesJsonSchemaInClasspath("post-schema.json"))
                .log().all()
                .extract().response();

        System.out.println("Response: " + response.asString());
    }
}
