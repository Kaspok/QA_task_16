package in.reqres.tests;

import com.github.javafaker.Faker;
import in.reqres.models.User;
import in.reqres.models.UserData;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class ApiTests {

    Faker faker = new Faker();

    private String name = faker.name().firstName();
    private String job = faker.job().position();
    private String email = faker.internet().emailAddress();

    @BeforeEach
    void beforeEach() {
        RestAssured.baseURI = "https://reqres.in/api";
    }

    @Test
    void singleUserCheckEmailAndTextTest() {
        given()
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body("data.email", equalTo("janet.weaver@reqres.in"))
                .body("support.text", equalTo("To keep ReqRes free, contributions towards server " +
                        "costs are appreciated!"));
    }

    @Test
    void listUserTest() {
        UserData listUser = given()
                .when()
                .get("/users?page=2")
                .then()
                .statusCode(200)
                .extract().response().as(UserData.class);

        Optional<User> first = listUser
                .getUsers()
                .stream()
                .filter(user -> user.getId() == 11).findFirst();

        assertThat(first.get().getFullName()).isEqualTo("George Edwards");
    }


    @Test
    void createUserTest() {
        JSONObject userData = new JSONObject()
                .put("name", this.name)
                .put("job", this.job);

        given()
                .contentType(ContentType.JSON)
                .body(userData.toString())
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo(name))
                .body("job", equalTo(job))
                .body("id", notNullValue());
    }

    @Test
    void negativeLoginTest() {
        JSONObject email = new JSONObject()
                .put("email", this.email);

        String response = given()
                .contentType(ContentType.JSON)
                .body(email.toString())
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .extract().response().jsonPath().getString("error");

        assertThat(response).isEqualTo("Missing password");

    }
}
