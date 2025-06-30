package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.handler.OrderBookLimitMatchingEngine;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.OrderBookMarketMatchingEngine;
import shop.shportfolio.trading.application.ports.input.LimitOrderExecutionUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;

import java.util.List;

@Slf4j
@Component
public class LimitOrderExecutionFacade implements LimitOrderExecutionUseCase {

    private final OrderBookManager orderBookManager;
    private final TemporaryKafkaPublisher kafkaProducer;
    private final OrderBookLimitMatchingEngine orderBookLimitMatchingEngine;

    @Autowired
    public LimitOrderExecutionFacade(OrderBookManager orderBookManager, TemporaryKafkaPublisher kafkaProducer,
                                     OrderBookLimitMatchingEngine orderBookLimitMatchingEngine) {
        this.orderBookManager = orderBookManager;
        this.kafkaProducer = kafkaProducer;
        this.orderBookLimitMatchingEngine = orderBookLimitMatchingEngine;
    }

    @Override
    public void executeLimitOrder(LimitOrder limitOrder) {
        MarketItem marketItem = orderBookManager.findMarketItemById(limitOrder.getMarketId().getValue());
        OrderBook reflectedOrderBook = orderBookManager.loadAdjustedOrderBook(marketItem.getId().getValue(),
                marketItem.getTickPrice().getValue());

        List<TradingRecordedEvent> tradingRecordedEvents;
        if (limitOrder.isBuyOrder()) {
            log.info("limit order is buy");
            tradingRecordedEvents = orderBookLimitMatchingEngine.execAsksLimitOrder(reflectedOrderBook, limitOrder);
        } else {
            tradingRecordedEvents = orderBookLimitMatchingEngine.execBidLimitOrder(reflectedOrderBook, limitOrder);
            log.info("limit order is sell");
        }

        tradingRecordedEvents.forEach(kafkaProducer::publish);
    }
}
