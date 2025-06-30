package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.OrderBookMarketMatchingEngine;
import shop.shportfolio.trading.application.ports.input.MarketOrderExecutionUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;

import java.util.List;

@Slf4j
@Component
public class MarketOrderExecutionFacade implements MarketOrderExecutionUseCase {

    private final OrderBookManager orderBookManager;
    private final TemporaryKafkaPublisher kafkaProducer;
    private final OrderBookMarketMatchingEngine orderBookMarketMatchingEngine;

    public MarketOrderExecutionFacade(OrderBookManager orderBookManager,
                                      TemporaryKafkaPublisher kafkaProducer,
                                      OrderBookMarketMatchingEngine orderBookMarketMatchingEngine){
        this.orderBookManager = orderBookManager;
        this.kafkaProducer = kafkaProducer;
        this.orderBookMarketMatchingEngine = orderBookMarketMatchingEngine;
    }

    @Override
    public void executeMarketOrder(MarketOrder marketOrder) {
        MarketItem marketItem = orderBookManager.findMarketItemById(marketOrder.getMarketId().getValue());
        // 여기서 Trade 결과값을 가져와서 orderBook에 반영해야 됌
        OrderBook reflectedOrderBook = orderBookManager.loadAdjustedOrderBook(marketItem.getId().getValue(),
                marketItem.getTickPrice().getValue());

        List<TradingRecordedEvent> tradingRecordedEvents;
        // 여기서 파라미터로 들어가는 오더북은 Trade 결과값이 반영된 자체 호가창임
        if (marketOrder.isBuyOrder()) {
            log.info("market order is buy");
            tradingRecordedEvents = orderBookMarketMatchingEngine.execAsksMarketOrder(reflectedOrderBook, marketOrder);
        } else {
            log.info("market order is sell");
            tradingRecordedEvents = orderBookMarketMatchingEngine.execBidMarketOrder(reflectedOrderBook, marketOrder);
        }
        // 임시 발행
        log.info("tradingRecordedEvents's size is -> {}", tradingRecordedEvents.size());
        tradingRecordedEvents.forEach(kafkaProducer::publish);
    }
}
