import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.Courier;
import ru.yandex.practicum.steps.CourierSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestLoginCourier extends BaseTest{

    private Courier courier;
    private Faker faker = new Faker();
    private CourierSteps courierSteps = new CourierSteps();
    private String loginToDelete;
    private String passwordToDelete;

    @Before
    public void setUp() {
        courier = new Courier();
        courier.withLogin(faker.name().username())
                .withPassword(faker.beer().name())
                .withFirstName(faker.name().firstName());
        loginToDelete = courier.getLogin();
        passwordToDelete = courier.getPassword();
        courierSteps.createCourier(courier);
    }

    @Test
    @DisplayName("Тест на логин курьера")
    public void loginCourier(){
        courierSteps.loginCourier(courier)
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Тест на логин курьера с пустым полем логин")
    public void cantLoginWithoutLogin(){
        courier.withLogin("");
        courierSteps.loginCourier(courier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест на логин курьера с пустым полем пароль")
    public void cantLoginWithoutPassword(){
        courier.withPassword("");
        courierSteps.loginCourier(courier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест на логин курьера с неверным логином")
    public void cantLoginWithFakeLogin(){
        courier.withLogin("Baobab228");
        courierSteps.loginCourier(courier)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Тест на логин курьера с неверным паролем")
    public void cantLoginWithFakePassword(){
        courier.withPassword("0123040501");
        courierSteps.loginCourier(courier)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void tearDown(){
        courier.withLogin(loginToDelete)
                .withPassword(passwordToDelete);
        courier.withId(courierSteps
                .loginCourier(courier)
                .extract().body().path("id"));
        courierSteps.deleteCourier(courier);
    }
}

