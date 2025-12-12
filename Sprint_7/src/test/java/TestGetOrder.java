import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.yandex.practicum.steps.OrderSteps;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.apache.http.HttpStatus.*;

public class TestGetOrder extends BaseTest{
    private final OrderSteps orderSteps = new OrderSteps();

    @Test
    @DisplayName("Тест на получение списка заказов")
    public void getOrder() {
        orderSteps.getOrderList()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders.size()", is(greaterThan(0)));
    }
}
