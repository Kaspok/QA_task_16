package in.reqres.tests;

import com.github.javafaker.Faker;
import in.reqres.models.User;
import in.reqres.models.UserData;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class ApiTests {

    Faker faker = new Faker();
    Date date = new Date();
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");

    private String name = faker.name().firstName();
    private String job = faker.job().position();
    private String email = faker.internet().emailAddress();

    JSONObject userData = new JSONObject()
            .put("name", this.name)
            .put("job", this.job);

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
        given()
                .contentType(ContentType.JSON)
                .body(this.userData.toString())
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

    @Test
    void deleteTest() {
        String response = given()
                .when()
                .delete("/users/2")
                .then()
                .statusCode(204)
                .extract().response().asString();

        assertThat(response).isEmpty();
    }

    @Test
    void updateUserTest() {
        this.formatDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        String response = given()
                .contentType(ContentType.JSON)
                .body(this.userData.toString())
                .when()
                .put("users/2")
                .then()
                .statusCode(200)
                .extract().response().jsonPath().getString("updatedAt");
        System.out.println(response);

        assertThat(response).contains(formatDate.format(date));
    }
}
