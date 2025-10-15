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

public class UserRegistrationTest {
    private final UserSteps userSteps = new UserSteps();
    private final Faker faker = new Faker();

    @Before
    public void setUp() {
        URLBase.setUp();
    }




    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания уникального пользователя")
    public void uniqueUserRegistration() {
        User user = new User(
                faker.internet().safeEmailAddress(),
                faker.internet().password(),
                faker.name().name()
        );

        userSteps.setUser(user);
        userSteps.registerUser()
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
    @DisplayName("Создание уже зарегистрированного пользователя")
    @Description("Проверка попытки создать уже существующего пользователя")
    public void existingUserRegistration() {
        User user = new User(
                faker.internet().safeEmailAddress(),
                faker.internet().password(),
                faker.name().name()
        );

        userSteps.setUser(user);
        userSteps.registerUser();
        userSteps.registerUser()
                .then()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля")
    @Description("Проверка регистрации без заполнения обязательного поля")
    public void incompleteUserRegistration() {
        User user = new User(
                "",
                faker.internet().password(),
                faker.name().name()
        );

        userSteps.setUser(user);
        userSteps.registerUser()
                .then()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка регистрации без заполнения пароля")
    public void registrationWithoutPassword() {
        User user = new User(
                faker.internet().safeEmailAddress(),
                "",
                faker.name().name()
        );

        userSteps.setUser(user);
        userSteps.registerUser()
                .then()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @Test
    @DisplayName("Создание пользователя без логина")
    @Description("Проверка регистрации без заполнения логина")
    public void registrationWithoutEmail() {
        User user = new User(
                "",
                faker.internet().password(),
                faker.name().name()
        );

        userSteps.setUser(user);
        userSteps.registerUser()
                .then()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @After
    public void tearDown() {
        if (userSteps.getCurrentUser() != null) {
            userSteps.deleteUser();
        }
    }


}

