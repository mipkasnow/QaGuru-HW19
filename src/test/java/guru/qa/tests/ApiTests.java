package guru.qa.tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static guru.qa.helpers.CustomAllureRestListener.withCustomTemplates;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ApiTests {

    @Test
    void checkThatUser2IsJanetWeaverTest() {
        Response response = (Response) given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().body()
                .when()
                .get("https://reqres.in/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/users2_schema.json"))
                .body("data.first_name", is("Janet"))
                .body("data.last_name", is("Weaver"))
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        String firstName = jsonPath.get("data.first_name");
        String lastName = jsonPath.get("data.last_name");

        assertThat(firstName).isEqualTo("Janet");
        assertThat(lastName).isEqualTo("Weaver");
    }

    @Test
    void checkThatThirdUserIsTobiasTest() {
        Response response = (Response) given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().body()
                .when()
                .get("https://reqres.in/api/users?page=2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<String> names = jsonPath.get("data.first_name");

        assertThat(names.get(2)).isEqualTo("Tobias");
    }

    @Test
    void createUserGeraltWitcherTest(){
        Map<String, String> user = new HashMap<>();
        user.put("name", "Geralt");
        user.put("job", "Witcher");

        given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().body()
                .contentType(JSON)
                .body(user)
                .when()
                .post("https://reqres.in/api/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    void incorrectLoginTest(){
        Map<String, String> user = new HashMap<>();
        user.put("email", "peter@klaven");
        Map<String, String> error = new HashMap<>();
        error.put("error", "Missing password");

        given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().body()
                .contentType(JSON)
                .body(user)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is(error.get("error")));
    }
}
