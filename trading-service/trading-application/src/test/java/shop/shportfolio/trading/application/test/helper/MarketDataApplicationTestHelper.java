package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.trading.application.MarketDataApplicationServiceImpl;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.usecase.TradingTrackUseCaseImpl;
import shop.shportfolio.trading.application.orderbook.manager.OrderBookManager;
import shop.shportfolio.trading.application.handler.track.MarketDataTrackHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.input.MarketDataApplicationService;
import shop.shportfolio.trading.application.ports.input.TradingTrackUseCase;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.OrderDomainServiceImpl;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.TradeDomainServiceImpl;

public class MarketDataApplicationTestHelper {
    public TradingDtoMapper tradingDtoMapper;

    public MarketDataApplicationService createMarketDataApplicationService(
            TradingOrderRepositoryPort orderRepo,
            TradingTradeRecordRepositoryPort tradeRecordRepo,
            TradingOrderRedisPort orderRedis,
            TradingMarketDataRepositoryPort marketRepo,
            BithumbApiPort bithumbApiPort
            ) {

        tradingDtoMapper = new TradingDtoMapper();
        TradeDomainService tradeDomainService = new TradeDomainServiceImpl();
        OrderDomainService orderDomainService = new OrderDomainServiceImpl();
        TradingTrackHandler trackHandler = new TradingTrackHandler(orderRepo, tradeRecordRepo, marketRepo);
        OrderBookManager orderBookManager = new OrderBookManager(orderDomainService,
                orderRedis, tradeRecordRepo, marketRepo, tradeDomainService);
        MarketDataTrackHandler marketDataTrackHandler = new MarketDataTrackHandler(
                marketRepo, tradeRecordRepo);
        TradingTrackUseCase trackUseCase = new TradingTrackUseCaseImpl(trackHandler, orderBookManager,
                marketDataTrackHandler,tradingDtoMapper,bithumbApiPort);
        return new MarketDataApplicationServiceImpl(trackUseCase, new TradingDataMapper());
    }

}