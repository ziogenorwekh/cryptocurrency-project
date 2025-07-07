package shop.shportfolio.trading.application.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.OrderBookReservationMatchingEngine;
import shop.shportfolio.trading.application.ports.input.ReservationOrderExecutionUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;

import java.util.List;

@Slf4j
@Component
public class ReservationOrderExecutionFacade implements ReservationOrderExecutionUseCase {

    private final OrderBookManager orderBookManager;
    private final TradeKafkaPublisher kafkaProducer;
    private final OrderBookReservationMatchingEngine orderBookReservationMatchingEngine;
    public ReservationOrderExecutionFacade(OrderBookManager orderBookManager, TradeKafkaPublisher kafkaProducer,
                                           OrderBookReservationMatchingEngine orderBookReservationMatchingEngine) {
        this.orderBookManager = orderBookManager;
        this.kafkaProducer = kafkaProducer;
        this.orderBookReservationMatchingEngine = orderBookReservationMatchingEngine;
    }

    @Override
    public void executeReservationOrder(ReservationOrder reservationOrder) {
        MarketItem marketItem = orderBookManager.findMarketItemById(reservationOrder.getMarketId().getValue());
        // 여기서 Trade 결과값을 가져와서 orderBook에 반영해야 됌
        OrderBook reflectedOrderBook = orderBookManager.loadAdjustedOrderBook(marketItem.getId().getValue(),
                marketItem.getTickPrice().getValue());

        List<TradingRecordedEvent> tradingRecordedEvents;
        if (reservationOrder.isBuyOrder()) {
            log.info("market order is buy");
            tradingRecordedEvents = orderBookReservationMatchingEngine
                    .execAsksReservationOrder(reflectedOrderBook, reservationOrder);
        } else {
            log.info("market order is sell");
            tradingRecordedEvents = orderBookReservationMatchingEngine
                    .execBidReservationOrder(reflectedOrderBook, reservationOrder);
        }
        log.info("tradingRecordedEvents's size is -> {}", tradingRecordedEvents.size());
        tradingRecordedEvents.forEach(kafkaProducer::publish);
    }
}
