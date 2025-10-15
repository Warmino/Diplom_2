package steps;

import config.URLBase;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Order;

public class OrderSteps extends URLBase {
    private final static String CREATE_ORDER_ENDPOINT = "/api/orders";
    private final static String INGREDIENTS_ENDPOINT = "/api/ingredients";

    @Step("Создание заказа")
    public Response createOrder(Order order) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post(CREATE_ORDER_ENDPOINT);
    }

    @Step("Получение списка ингредиентов")
    public Response getIngredientsList() {
        return RestAssured.given()
                .when()
                .get(INGREDIENTS_ENDPOINT);
    }
}