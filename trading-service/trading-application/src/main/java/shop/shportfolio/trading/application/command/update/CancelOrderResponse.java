package shop.shportfolio.trading.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;

@Getter
@Builder
@AllArgsConstructor
public class CancelOrderResponse {

    private final String orderId;
    private final OrderStatus orderStatus;
}
