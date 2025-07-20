package shop.shportfolio.trading.application.validator;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.exception.OrderInValidatedException;
import shop.shportfolio.trading.application.exception.UserBalanceNotFoundException;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.policy.FeePolicy;
import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MarketOrderValidator implements OrderValidator<MarketOrder> {


    private final OrderBookManager orderBookManager;
    public MarketOrderValidator(OrderBookManager orderBookManager) {
        this.orderBookManager = orderBookManager;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.MARKET.equals(order.getOrderType());
    }

    @Override
    public void validateBuyOrder(MarketOrder order, MarketItem marketItem) {
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        BigDecimal totalAvailableQty = orderBook.getSellPriceLevels()
                .values()
                .stream()
                .flatMap(priceLevel -> priceLevel.getOrders().stream())
                .map(orderInBook -> orderInBook.getRemainingQuantity().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailableQty.compareTo(BigDecimal.ZERO) == 0) {
            throw new OrderInValidatedException("No available sell liquidity for market buy order.");
        }
    }
    @Override
    public void validateSellOrder(MarketOrder order, MarketItem marketItem) {
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        BigDecimal totalAvailableQty = orderBook.getBuyPriceLevels()
                .values()
                .stream()
                .flatMap(priceLevel -> priceLevel.getOrders().stream())
                .map(orderInBook -> orderInBook.getRemainingQuantity().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailableQty.compareTo(BigDecimal.ZERO) == 0) {
            throw new OrderInValidatedException("No available buy liquidity for market sell order.");
        }
    }

}
