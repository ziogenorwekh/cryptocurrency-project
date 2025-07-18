package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.trading.application.MarketDataApplicationServiceImpl;
import shop.shportfolio.trading.application.facade.TradingTrackFacade;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.track.MarketDataTrackHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.input.MarketDataApplicationService;
import shop.shportfolio.trading.application.ports.input.TradingTrackUseCase;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.OrderDomainServiceImpl;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.TradeDomainServiceImpl;

public class MarketDataApplicationTestHelper {
    public static TradingDtoMapper tradingDtoMapper = new TradingDtoMapper();

    public static MarketDataApplicationService createMarketDataApplicationService(
            TradingOrderRepositoryPort orderRepo,
            TradingTradeRecordRepositoryPort tradeRecordRepo,
            TradingOrderRedisPort orderRedis,
            TradingMarketDataRepositoryPort marketRepo,
            TradingMarketDataRedisPort marketDataRedis,
            BithumbApiPort bithumbApiPort) {

        TradeDomainService tradeDomainService = new TradeDomainServiceImpl();
        OrderDomainService orderDomainService = new OrderDomainServiceImpl();
        TradingTrackHandler trackHandler = new TradingTrackHandler(orderRepo, tradeRecordRepo, marketRepo);
        OrderBookManager orderBookManager = new OrderBookManager(orderDomainService,
                tradingDtoMapper, orderRedis, marketDataRedis, tradeRecordRepo, marketRepo, tradeDomainService);
        MarketDataTrackHandler marketDataTrackHandler = new MarketDataTrackHandler(bithumbApiPort
                , tradingDtoMapper, marketRepo, tradeRecordRepo);
        TradingTrackUseCase trackUseCase = new TradingTrackFacade(trackHandler, orderBookManager, marketDataTrackHandler);
        return new MarketDataApplicationServiceImpl(trackUseCase, new TradingDataMapper());
    }

}