package shop.shportfolio.trading.application.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.exception.OrderInValidatedException;
import shop.shportfolio.trading.application.orderbook.manager.OrderBookManager;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;

@Slf4j
@Component
public class MarketOrderValidator implements OrderValidator<MarketOrder> {

    private final BithumbApiPort bithumbApiPort;
    public MarketOrderValidator(BithumbApiPort bithumbApiPort) {
        this.bithumbApiPort = bithumbApiPort;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.MARKET.equals(order.getOrderType());
    }

    @Override
    public void validateBuyOrder(MarketOrder order) {
        OrderBookBithumbDto bookBithumbDto = bithumbApiPort.findOrderBookByMarketId(order.getMarketId().getValue());
        Double totalAskSize = bookBithumbDto.getTotalAskSize();
        log.info("totalAskSize = {}", totalAskSize);
        log.info("marketOrder Price is : {}",order.getOrderPrice().getValue());
        if (BigDecimal.valueOf(totalAskSize).compareTo(order.getOrderPrice().getValue()) < 0) {
            throw new OrderInValidatedException("Requested buy amount exceeds available sell liquidity.");
        }
    }
    @Override
    public void validateSellOrder(MarketOrder order) {
        OrderBookBithumbDto bookBithumbDto = bithumbApiPort.findOrderBookByMarketId(order.getMarketId().getValue());
        Double totalBidSize = bookBithumbDto.getTotalBidSize();
        if (BigDecimal.valueOf(totalBidSize).compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderInValidatedException("Requested sell amount exceeds available buy liquidity.");
        }
    }

}
