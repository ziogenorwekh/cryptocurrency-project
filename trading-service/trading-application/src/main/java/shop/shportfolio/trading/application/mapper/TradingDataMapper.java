package shop.shportfolio.trading.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.domain.entity.LimitOrder;

@Component
public class TradingDataMapper {

    public CreateLimitOrderResponse LimitOrderToCreateLimitOrderResponse(LimitOrder limitOrder) {
        return new CreateLimitOrderResponse(
                limitOrder.getUserId().getValue(), limitOrder.getMarketId().getValue(), limitOrder.getOrderPrice().getValue()
                , limitOrder.getOrderSide().getValue(), limitOrder.getQuantity().getValue(), limitOrder.getOrderType()
        );
    }

}
