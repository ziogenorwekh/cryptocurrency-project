package shop.shportfolio.trading.application.validator;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;

@Component
public class ReservationOrderValidator implements OrderValidator<ReservationOrder> {

    private final OrderBookManager orderBookManager;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;

    public ReservationOrderValidator(OrderBookManager orderBookManager,
                                     TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort) {
        this.orderBookManager = orderBookManager;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.RESERVATION.equals(order.getOrderType());
    }

    @Override
    public boolean validateBuyOrder(ReservationOrder order) {
        MarketItem marketItem = tradingMarketDataRepositoryPort
                .findMarketItemByMarketId(order.getMarketId().getValue())
                .orElseThrow(() -> new MarketItemNotFoundException(
                        String.format("%s is not found", order.getMarketId().getValue())));

        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        BigDecimal totalAvailableQty = orderBook.getSellPriceLevels()
                .values()
                .stream()
                .flatMap(priceLevel -> priceLevel.getOrders().stream())
                .map(orderInBook -> orderInBook.getRemainingQuantity().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return order.getQuantity().getValue().compareTo(totalAvailableQty) <= 0;
    }

    @Override
    public boolean validateSellOrder(ReservationOrder order) {
        MarketItem marketItem = tradingMarketDataRepositoryPort
                .findMarketItemByMarketId(order.getMarketId().getValue())
                .orElseThrow(() -> new MarketItemNotFoundException(
                        String.format("%s is not found", order.getMarketId().getValue())));

        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        BigDecimal totalAvailableQty = orderBook.getBuyPriceLevels()
                .values()
                .stream()
                .flatMap(priceLevel -> priceLevel.getOrders().stream())
                .map(orderInBook -> orderInBook.getRemainingQuantity().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return order.getQuantity().getValue().compareTo(totalAvailableQty) <= 0;
    }


}
