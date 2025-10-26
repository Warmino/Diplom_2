package steps;

import config.URLBase;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;

public class UserSteps extends URLBase {
    private User user;
    private final static String REGISTER_ENDPOINT = "/api/auth/register";
    private final static String LOGIN_ENDPOINT = "/api/auth/login";
    private final static String DELETE_USER_ENDPOINT = "/api/auth/user";

    public void setUser(User user) {
        this.user = user;
    }

    @Step("Регистрация пользователя")
    public Response registerUser() {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT);
    }

    @Step("Авторизация пользователя")
    public Response loginUser() {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post(LOGIN_ENDPOINT);
    }

    @Step("Удаление пользователя")
    public Response deleteUser() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + getAccessToken())
                .when()
                .delete(DELETE_USER_ENDPOINT);
    }

    private String getAccessToken() {
        return user.getAccessToken();
    }


    public User getCurrentUser() {
        return user;
    }
}
