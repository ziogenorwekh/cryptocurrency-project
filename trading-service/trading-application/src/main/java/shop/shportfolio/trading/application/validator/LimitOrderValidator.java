package shop.shportfolio.trading.application.validator;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class LimitOrderValidator implements OrderValidator<LimitOrder> {

    private final OrderBookManager orderBookManager;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    public LimitOrderValidator(OrderBookManager orderBookManager,
                               TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort) {
        this.orderBookManager = orderBookManager;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.LIMIT.equals(order.getOrderType());
    }

    @Override
    public boolean validateBuyOrder(LimitOrder order) {
        MarketItem marketItem = tradingMarketDataRepositoryPort
                .findMarketItemByMarketId(order.getMarketId().getValue())
                .orElseThrow(() -> new MarketItemNotFoundException(
                        String.format("%s is not found", order.getMarketId().getValue())));
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        Map.Entry<TickPrice, PriceLevel> lowestAskEntry = orderBook.getSellPriceLevels().firstEntry();

        if (lowestAskEntry == null) {
            return true;
        }
        TickPrice lowestAskPrice = lowestAskEntry.getKey();
        return !isOverTenPercentHigher(order.getOrderPrice(),lowestAskPrice.getValue());
    }

    @Override
    public boolean validateSellOrder(LimitOrder order) {
        MarketItem marketItem = tradingMarketDataRepositoryPort
                .findMarketItemByMarketId(order.getMarketId().getValue())
                .orElseThrow(() -> new MarketItemNotFoundException(
                        String.format("%s is not found", order.getMarketId().getValue())));
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        Map.Entry<TickPrice, PriceLevel> lowestAskEntry = orderBook.getBuyPriceLevels().firstEntry();

        if (lowestAskEntry == null) {
            return true;
        }
        TickPrice lowestAskPrice = lowestAskEntry.getKey();
        return !isOverTenPercentLower(order.getOrderPrice(),lowestAskPrice.getValue());
    }


    private boolean isOverTenPercentHigher(OrderPrice price, BigDecimal reference) {
        BigDecimal diff = price.getValue().subtract(reference);
        if (diff.compareTo(BigDecimal.ZERO) <= 0) return false;
        BigDecimal ratio = diff.divide(reference, 8, RoundingMode.HALF_UP);
        return ratio.compareTo(new BigDecimal("0.1")) > 0;
    }

    private boolean isOverTenPercentLower(OrderPrice price, BigDecimal reference) {
        BigDecimal diff = reference.subtract(price.getValue());
        if (diff.compareTo(BigDecimal.ZERO) <= 0) return false;
        BigDecimal ratio = diff.divide(reference, 8, RoundingMode.HALF_UP);
        return ratio.compareTo(new BigDecimal("0.1")) > 0;
    }
}
