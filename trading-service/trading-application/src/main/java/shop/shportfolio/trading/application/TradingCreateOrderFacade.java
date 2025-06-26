package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.exception.OrderBookNotFoundException;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;

import java.util.List;

@Slf4j
@Component
public class TradingCreateOrderFacade implements TradingCreateOrderUseCase {


    private final TradingCreateHandler tradingCreateHandler;
    private final MarketDataRedisAdapter marketDataRedisAdapter;
    private final TemporaryKafkaPublisher kafkaProducer;
    private final TradingDtoMapper tradingDtoMapper;

    @Autowired
    public TradingCreateOrderFacade(TradingCreateHandler tradingCreateHandler,
                                    MarketDataRedisAdapter marketDataRedisAdapter,
                                    TemporaryKafkaPublisher kafkaProducer,
                                    TradingDtoMapper tradingDtoMapper) {
        this.tradingCreateHandler = tradingCreateHandler;
        this.marketDataRedisAdapter = marketDataRedisAdapter;
        this.kafkaProducer = kafkaProducer;
        this.tradingDtoMapper = tradingDtoMapper;
    }

    @Override
    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        LimitOrder limitOrder = tradingCreateHandler.createLimitOrder(command);
        marketDataRedisAdapter.saveLimitOrder(RedisKeyPrefix.limit(limitOrder.getMarketId().getValue()), limitOrder);
        return limitOrder;
    }

    @Override
    public void createMarketOrder(CreateMarketOrderCommand command) {
        MarketOrder marketOrder = tradingCreateHandler.createMarketOrder(command);

        MarketItem marketItem = tradingCreateHandler.findMarketItemById(new MarketId(command.getMarketId()));
        OrderBookDto orderBookDto = marketDataRedisAdapter
                .findOrderBookByMarket(marketItem.getId().getValue()).orElseThrow(() ->
                        new OrderBookNotFoundException(String.format("Market id %s not found",
                                command.getMarketId())));
        OrderBook orderBook = tradingDtoMapper.orderBookDtoToOrderBook(orderBookDto,
                marketItem.getTickPrice().getValue());

        List<TradingRecordedEvent> tradingRecordedEvents;
        if (marketOrder.isBuyOrder()) {
            log.info("market order is buy");
            tradingRecordedEvents = tradingCreateHandler.execAsksMarketOrder(orderBook, marketOrder);
        } else {
            log.info("market order is sell");
            tradingRecordedEvents = tradingCreateHandler.execBidMarketOrder(orderBook, marketOrder);
        }
        // 임시 발행
        tradingRecordedEvents.forEach(kafkaProducer::publish);
    }
}
