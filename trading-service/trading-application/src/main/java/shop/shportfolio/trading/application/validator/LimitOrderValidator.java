package shop.shportfolio.trading.application.validator;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.exception.OrderInValidatedException;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.policy.LiquidityPolicy;
import shop.shportfolio.trading.application.policy.PriceLimitPolicy;
import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class LimitOrderValidator implements OrderValidator<LimitOrder> {

    private final OrderBookManager orderBookManager;
    private final PriceLimitPolicy priceLimitPolicy;
    private final LiquidityPolicy liquidityPolicy;
    public LimitOrderValidator(OrderBookManager orderBookManager,
                               PriceLimitPolicy priceLimitPolicy,
                               LiquidityPolicy liquidityPolicy) {
        this.orderBookManager = orderBookManager;
        this.priceLimitPolicy = priceLimitPolicy;
        this.liquidityPolicy = liquidityPolicy;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.LIMIT.equals(order.getOrderType());
    }

    @Override
    public void validateBuyOrder(LimitOrder order, MarketItem marketItem) {
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        Map.Entry<TickPrice, PriceLevel> lowestAskEntry = orderBook.getSellPriceLevels().firstEntry();

        BigDecimal totalAvailableQty = liquidityPolicy.calculateTotalAvailableSellQuantity(orderBook);

        if (order.getQuantity().getValue().compareTo(totalAvailableQty) > 0) {
            throw new OrderInValidatedException("Buy order quantity exceeds available sell liquidity.");
        }

        if (lowestAskEntry == null) {
            return; // 주문 가능
        }
        TickPrice lowestAskPrice = lowestAskEntry.getKey();
        if (priceLimitPolicy.isOverTenPercentHigher(order.getOrderPrice(), lowestAskPrice.getValue())) {
            throw new OrderInValidatedException("Limit buy order price is more than 10% above best ask.");
        }
    }

    @Override
    public void validateSellOrder(LimitOrder order,MarketItem marketItem) {
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        BigDecimal totalAvailableQty = liquidityPolicy.calculateTotalAvailableBuyQuantity(orderBook);

        if (order.getQuantity().getValue().compareTo(totalAvailableQty) > 0) {
            throw new OrderInValidatedException("Sell order quantity exceeds available buy liquidity.");
        }

        Map.Entry<TickPrice, PriceLevel> lowestAskEntry = orderBook.getBuyPriceLevels().lastEntry();

        if (lowestAskEntry == null) {
            return; // 주문 가능
        }
        TickPrice lowestAskPrice = lowestAskEntry.getKey();
        if (priceLimitPolicy.isOverTenPercentLower(order.getOrderPrice(), lowestAskPrice.getValue())) {
            throw new OrderInValidatedException("Limit sell order price is more than 10% below best bid.");
        }
    }
}
