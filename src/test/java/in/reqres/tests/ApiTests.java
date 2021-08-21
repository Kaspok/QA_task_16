package in.reqres.tests;

import in.reqres.models.User;
import in.reqres.models.UserData;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ApiTests {

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
}
