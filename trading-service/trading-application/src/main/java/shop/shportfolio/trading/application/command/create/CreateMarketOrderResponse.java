package shop.shportfolio.trading.application.command.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateMarketOrderResponse {

    private final UUID userId;
    private final String orderId;
    private final String marketId;
    private final BigDecimal orderedPrice;
    private final String orderSide;
    private final OrderType orderType;
}
