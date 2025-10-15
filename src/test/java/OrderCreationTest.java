import com.github.javafaker.Faker;
import config.URLBase;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.Order;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class OrderCreationTest {
    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private final Faker faker = new Faker();

    @Before
    public void setUp() {
        URLBase.setUp();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка возможности создания заказа с авторизацией")
    public void authorizedOrderCreation() {

        User user = new User(faker.internet().safeEmailAddress(), faker.internet().password(), faker.name().firstName());
        userSteps.setUser(user);
        userSteps.registerUser();
        userSteps.loginUser();


        Response response = orderSteps.getIngredientsList();
        List<String> ingredientIds = extractIngredientIds(response.body().asString());


        Order order = new Order(ingredientIds.subList(0, Math.min(ingredientIds.size(), 3)));
        orderSteps.createOrder(order)
                .then()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка невозможности создания заказа без авторизации")
    public void unauthorizedOrderCreation() {

        Response response = orderSteps.getIngredientsList();
        List<String> ingredientIds = extractIngredientIds(response.body().asString());


        Order order = new Order(ingredientIds.subList(0, Math.min(ingredientIds.size(), 3)));


        orderSteps.createOrder(order)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    @Description("Проверка создания заказа с корректными ингредиентами")
    public void validOrderWithIngredients() {

        User user = new User(faker.internet().safeEmailAddress(), faker.internet().password(), faker.name().firstName());
        userSteps.setUser(user);
        userSteps.registerUser();
        userSteps.loginUser();


        Response response = orderSteps.getIngredientsList();
        List<String> ingredientIds = extractIngredientIds(response.body().asString());


        Order order = new Order(ingredientIds.subList(0, Math.min(ingredientIds.size(), 3)));
        orderSteps.createOrder(order)
                .then()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка создания заказа без ингредиентов")
    public void invalidOrderWithoutIngredients() {

        User user = new User(faker.internet().safeEmailAddress(), faker.internet().password(), faker.name().firstName());
        userSteps.setUser(user);
        userSteps.registerUser();
        userSteps.loginUser();


        Order emptyOrder = new Order(null);
        orderSteps.createOrder(emptyOrder)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка создания заказа с неверными ингредиентами")
    public void invalidOrderWithInvalidIngredients() {

        User user = new User(faker.internet().safeEmailAddress(), faker.internet().password(), faker.name().firstName());
        userSteps.setUser(user);
        userSteps.registerUser();
        userSteps.loginUser();


        Order invalidOrder = new Order(Arrays.asList("invalid-hash-1", "invalid-hash-2"));
        Response response = orderSteps.createOrder(invalidOrder);


        System.out.println("Тело ответа:");
        System.out.println(response.prettyPrint());


        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown() {
        if (userSteps.getCurrentUser() != null) {
            userSteps.deleteUser();
        }
    }


    private List<String> extractIngredientIds(String jsonResponseBody) {
        JsonPath jsonPath = new JsonPath(jsonResponseBody);
        return jsonPath.getList("data._id");
    }
}