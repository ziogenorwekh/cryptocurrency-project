package shop.shportfolio.trading.application;

import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.exception.OrderBookNotFoundException;
import shop.shportfolio.trading.application.handler.TradingCreateHandler;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaProducer;
import shop.shportfolio.trading.application.ports.output.redis.TradingDataRedisRepositoryAdapter;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Trade;

import java.util.List;

@Component
public class TradingCreateOrderFacade implements TradingCreateOrderUseCase {


    private final TradingCreateHandler tradingCreateHandler;
    private final TradingDataRedisRepositoryAdapter tradingDataRedisRepositoryAdapter;
    private final TemporaryKafkaProducer kafkaProducer;

    @Autowired
    public TradingCreateOrderFacade(TradingCreateHandler tradingCreateHandler,
                                    TradingDataRedisRepositoryAdapter tradingDataRedisRepositoryAdapter,
                                    TemporaryKafkaProducer kafkaProducer) {
        this.tradingCreateHandler = tradingCreateHandler;
        this.tradingDataRedisRepositoryAdapter = tradingDataRedisRepositoryAdapter;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        return tradingCreateHandler.createLimitOrder(command);
    }

    @Override
    public void createMarketOrder(CreateMarketOrderCommand command) {
        OrderBookDto orderBookDto = tradingDataRedisRepositoryAdapter
                .findOrderBookByMarket(command.getMarketId()).orElseThrow(() -> {
                    throw new OrderBookNotFoundException(String.format("Market id %s not found",
                            command.getMarketId()));
                });
        MarketOrder marketOrder = tradingCreateHandler.createMarketOrder(command);
        List<Trade> trades = tradingCreateHandler.execMarketOrder(orderBookDto.getAsks(), marketOrder);
        // 임시 발행
        kafkaProducer.publish();
    }
}
