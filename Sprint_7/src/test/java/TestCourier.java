import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.Courier;
import ru.yandex.practicum.steps.CourierSteps;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class TestCourier extends BaseTest {

    private Courier courier;
    private Faker faker = new Faker();
    private CourierSteps courierSteps = new CourierSteps();
    private boolean shouldDeleteCourier = true;
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
    }

    @Test
    @DisplayName("Тест на добавление курьера")
    public void addedNewCourier() {
        courierSteps.createCourier(courier)
                .statusCode(201)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Тест на добавление курьера с пустым полем логина")
    public void cantAddCourierWithoutLogin() {
        courier.withLogin("");
        courierSteps.createCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        this.shouldDeleteCourier = false;
    }

    @Test
    @DisplayName("Тест на добавление курьера с пустым полем пароль")
    public void cantAddCourierWithoutPassword() {
        courier.withPassword("");
        courierSteps.createCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        this.shouldDeleteCourier = false;
    }

    @Test
    @DisplayName("Тест на добавление курьера с пустым полем Имя")
    public void cantAddCourierWithoutFirstName() {
        courier.withFirstName("");
        courierSteps.createCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        this.shouldDeleteCourier = false;
    }

    @Test
    @DisplayName("Тест на добавление курьера с существующим логином")
    public void cantAddCourierWithSameLogin() {
        courierSteps.createCourier(courier);
        courierSteps.createCourier(courier)
                .statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Тест на логин курьера")
    public void loginCourier(){
        courierSteps.createCourier(courier);
        courierSteps.loginCourier(courier)
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Тест на логин курьера с пустым полем логин")
    public void cantLoginWithoutLogin(){
        courierSteps.createCourier(courier);
        courier.withLogin("");
        courierSteps.loginCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест на логин курьера с пустым полем пароль")
    public void cantLoginWithoutPassword(){
        courierSteps.createCourier(courier);
        courier.withPassword("");
        courierSteps.loginCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест на логин курьера с неверным логином")
    public void cantLoginWithFakeLogin(){
        courierSteps.createCourier(courier);
        courier.withLogin("Baobab228");
        courierSteps.loginCourier(courier)
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Тест на логин курьера с неверным паролем")
    public void cantLoginWithFakePassword(){
        courierSteps.createCourier(courier);
        courier.withPassword("0123040501");
        courierSteps.loginCourier(courier)
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void tearDown(){
        courier.withLogin(loginToDelete)
                .withPassword(passwordToDelete);
        if(shouldDeleteCourier) {
            courier.withId(courierSteps
                    .loginCourier(courier)
                    .extract().body().path("id"));
            courierSteps.deleteCourier(courier);
        }
    }
}
