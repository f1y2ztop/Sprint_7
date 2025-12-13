import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.Courier;
import ru.yandex.practicum.steps.CourierSteps;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.apache.http.HttpStatus.*;

public class TestCreateCourier extends BaseTest {

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
                .statusCode(SC_CREATED)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Тест на добавление курьера с пустым полем логина")
    public void cantAddCourierWithoutLogin() {
        courier.withLogin("");
        courierSteps.createCourier(courier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        this.shouldDeleteCourier = false;
    }

    @Test
    @DisplayName("Тест на добавление курьера с пустым полем пароль")
    public void cantAddCourierWithoutPassword() {
        courier.withPassword("");
        courierSteps.createCourier(courier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        this.shouldDeleteCourier = false;
    }

    @Test
    @DisplayName("Тест на добавление курьера с пустым полем Имя")
    public void cantAddCourierWithoutFirstName() {
        courier.withFirstName("");
        courierSteps.createCourier(courier)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        this.shouldDeleteCourier = false;
    }

    @Test
    @DisplayName("Тест на добавление курьера с существующим логином")
    public void cantAddCourierWithSameLogin() {
        courierSteps.createCourier(courier);
        courierSteps.createCourier(courier)
                .statusCode(SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется"));
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
