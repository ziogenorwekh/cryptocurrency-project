package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.exception.OrderBookNotFoundException;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;

import java.util.List;

@Slf4j
@Component
public class TradingCreateOrderFacade implements TradingCreateOrderUseCase {


    private final TradingCreateHandler tradingCreateHandler;
    private final MarketDataRedisAdapter marketDataRedisAdapter;
    private final TemporaryKafkaPublisher kafkaProducer;

    @Autowired
    public TradingCreateOrderFacade(TradingCreateHandler tradingCreateHandler,
                                    MarketDataRedisAdapter marketDataRedisAdapter,
                                    TemporaryKafkaPublisher kafkaProducer) {
        this.tradingCreateHandler = tradingCreateHandler;
        this.marketDataRedisAdapter = marketDataRedisAdapter;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        LimitOrder limitOrder = tradingCreateHandler.createLimitOrder(command);
        marketDataRedisAdapter.saveLimitOrder(RedisKeyPrefix.limit(limitOrder.getMarketId().getValue()), limitOrder);
        return limitOrder;
    }

    @Override
    public void createMarketOrder(CreateMarketOrderCommand command) {
        OrderBookDto orderBookDto = marketDataRedisAdapter
                .findOrderBookByMarket(command.getMarketId()).orElseThrow(() ->
                        new OrderBookNotFoundException(String.format("Market id %s not found",
                                command.getMarketId())));
        MarketOrder marketOrder = tradingCreateHandler.createMarketOrder(command);
        List<TradingRecordedEvent> tradingRecordedEvents;
        if (marketOrder.isBuyOrder()) {
            log.info("market order is buy");
            tradingRecordedEvents = tradingCreateHandler.execAsksMarketOrder(orderBookDto.getAsks(), marketOrder);
        } else {
            log.info("market order is sell");
            tradingRecordedEvents = tradingCreateHandler.execBidMarketOrder(orderBookDto.getBids(), marketOrder);
        }
        // 임시 발행
        tradingRecordedEvents.forEach(kafkaProducer::publish);
    }
}
