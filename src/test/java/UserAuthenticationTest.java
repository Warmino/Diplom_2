import com.github.javafaker.Faker;
import config.URLBase;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class UserAuthenticationTest {
    private final UserSteps userSteps = new UserSteps();
    private final Faker faker = new Faker();

    @Before
    public void setUp() {
        URLBase.setUp();
    }





    @Test
    @DisplayName("Авторизация существующего пользователя")
    @Description("Проверка успешной авторизации пользователя")
    public void successfulUserLogin() {
        User user = new User(
                faker.internet().safeEmailAddress(),
                faker.internet().password(),
                faker.name().name()
        );

        userSteps.setUser(user);
        userSteps.registerUser();

        userSteps.loginUser()
                .then()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    @Description("Проверка неудачной авторизации с неправильным логином")
    public void wrongEmailLogin() {

        User correctUser = new User(
                faker.internet().safeEmailAddress(),
                faker.internet().password(),
                faker.name().name()
        );


        userSteps.setUser(correctUser);
        userSteps.registerUser();


        User wrongEmailUser = new User(
                faker.internet().safeEmailAddress(),
                correctUser.getPassword(),
                correctUser.getName()
        );

        userSteps.setUser(wrongEmailUser);
        userSteps.loginUser()
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    @Description("Проверка неудачной авторизации с неправильным паролем")
    public void wrongPasswordLogin() {

        User correctUser = new User(
                faker.internet().safeEmailAddress(),
                faker.internet().password(),
                faker.name().name()
        );


        userSteps.setUser(correctUser);
        userSteps.registerUser();


        User wrongPasswordUser = new User(
                correctUser.getEmail(),
                faker.internet().password(),
                correctUser.getName()
        );

        userSteps.setUser(wrongPasswordUser);
        userSteps.loginUser()
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
    @After
    public void tearDown() {
        if (userSteps.getCurrentUser() != null) {
            userSteps.deleteUser();
        }
    }
}
