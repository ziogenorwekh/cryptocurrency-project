package shop.shportfolio.trading.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.PriceLevel;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.BiFunction;

@Component
public class OrderBookReservationMatchingEngine {


    private final TradingDomainService tradingDomainService;
    private final TradingRepositoryAdapter tradingRepository;

    @Autowired
    public OrderBookReservationMatchingEngine(TradingDomainService tradingDomainService, TradingRepositoryAdapter tradingRepository) {
        this.tradingDomainService = tradingDomainService;
        this.tradingRepository = tradingRepository;
    }


    public List<TradingRecordedEvent> execBidReservationOrder(OrderBook orderBook, ReservationOrder reservationOrder) {
        return execLimitOrder(
                reservationOrder,
                orderBook, orderBook.getSellPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createTrade(
                        tradeId, reservationOrder.getUserId(), reservationOrder.getId(),
                        reservationOrder.getOrderPrice(), qty,
                        TransactionType.TRADE_SELL));
    }

    public List<TradingRecordedEvent> execAsksReservationOrder(OrderBook orderBook, ReservationOrder reservationOrder) {
        return execLimitOrder(
                reservationOrder, orderBook, orderBook.getBuyPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createTrade(
                        tradeId, reservationOrder.getUserId(), reservationOrder.getId(),
                        reservationOrder.getOrderPrice(), qty,
                        TransactionType.TRADE_BUY));
    }


    private List<TradingRecordedEvent> execLimitOrder(ReservationOrder reservationOrder,
                                                      OrderBook orderBook,
                                                      NavigableMap<TickPrice, PriceLevel> counterPriceLevels,
                                                      BiFunction<TradeId, Quantity, TradingRecordedEvent> tradeEventCreator) {
        List<TradingRecordedEvent> trades = new ArrayList<>();


        return trades;
    }
}
