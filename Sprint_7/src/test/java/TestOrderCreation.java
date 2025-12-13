import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.practicum.models.Order;
import ru.yandex.practicum.steps.OrderSteps;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
public class TestOrderCreation extends BaseTest{

    private final OrderSteps orderSteps = new OrderSteps();
    private final List<String> color;
    public TestOrderCreation(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет: {0}")
    public static Collection<Object[]> getColorData() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {null}
        });
    }
    private Order createOrder() {
        return new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                null
        );
    }

    @Test
    @DisplayName("Тест на создание заказа с разными расцветками самоката")
    public void successfulOrderCreation() {
        Order order = createOrder().withColor(this.color);
        orderSteps.createOrder(order)
        .statusCode(SC_CREATED)
        .body("track", notNullValue());
    }
}
