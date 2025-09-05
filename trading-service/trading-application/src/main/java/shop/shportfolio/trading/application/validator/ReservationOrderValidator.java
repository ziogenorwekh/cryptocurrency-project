package shop.shportfolio.trading.application.validator;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.exception.OrderInValidatedException;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;

@Component
public class ReservationOrderValidator implements OrderValidator<ReservationOrder> {

    private final BithumbApiPort bithumbApiPort;

    public ReservationOrderValidator(BithumbApiPort bithumbApiPort) {
        this.bithumbApiPort = bithumbApiPort;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.RESERVATION.equals(order.getOrderType());
    }

    @Override
    public void validateBuyOrder(ReservationOrder order) {
        OrderBookBithumbDto bookBithumbDto = bithumbApiPort.findOrderBookByMarketId(order.getMarketId().getValue());
        BigDecimal totalAvailableQty = BigDecimal.valueOf(bookBithumbDto.getTotalAskSize());
        if (order.getQuantity().getValue().compareTo(totalAvailableQty) > 0) {
            throw new OrderInValidatedException("Buy order quantity exceeds available sell liquidity.");
        }
    }

    @Override
    public void validateSellOrder(ReservationOrder order) {
        OrderBookBithumbDto bookBithumbDto = bithumbApiPort.findOrderBookByMarketId(order.getMarketId().getValue());
        BigDecimal totalAvailableQty = BigDecimal.valueOf(bookBithumbDto.getTotalBidSize());
        if (order.getQuantity().getValue().compareTo(totalAvailableQty) > 0) {
            throw new OrderInValidatedException("Sell order quantity exceeds available buy liquidity.");
        }
    }


}
