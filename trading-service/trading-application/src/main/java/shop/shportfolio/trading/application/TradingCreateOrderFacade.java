package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.*;

@Slf4j
@Component
public class TradingCreateOrderFacade implements TradingCreateOrderUseCase {


    private final TradingCreateHandler tradingCreateHandler;
    private final MarketDataRedisAdapter marketDataRedisAdapter;
    @Autowired
    public TradingCreateOrderFacade(TradingCreateHandler tradingCreateHandler,
                                    MarketDataRedisAdapter marketDataRedisAdapter){
        this.tradingCreateHandler = tradingCreateHandler;
        this.marketDataRedisAdapter = marketDataRedisAdapter;
    }

    @Override
    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        LimitOrder limitOrder = tradingCreateHandler.createLimitOrder(command);
        marketDataRedisAdapter.saveLimitOrder(RedisKeyPrefix.limit(limitOrder.getMarketId().getValue()), limitOrder);
        return limitOrder;
    }

    @Override
    public MarketOrder createMarketOrder(CreateMarketOrderCommand command) {
        return tradingCreateHandler.createMarketOrder(command);
    }
}
