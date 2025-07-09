package shop.shportfolio.trading.application.validator;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.util.Map;

@Component
public class LimitOrderValidator<T extends Order> implements OrderValidator<LimitOrder> {

    private final OrderBookManager orderBookManager;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    public LimitOrderValidator(OrderBookManager orderBookManager,
                               TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort) {
        this.orderBookManager = orderBookManager;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
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
        Quantity quantity = lowestAskEntry.getValue().peekOrder().getQuantity();


        if (lowestAskEntry == null) {
            return true;
        }

        TickPrice lowestAskPrice = lowestAskEntry.getKey();
        return !order.getOrderPrice().isOverTenPercent(lowestAskPrice.getValue());
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
        return !order.getOrderPrice().isUnderTenPercent(lowestAskPrice.getValue());
    }
}
