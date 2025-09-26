package shop.shportfolio.matching.application.test.helper;

import shop.shportfolio.matching.application.handler.OrderBookManager;
import shop.shportfolio.matching.application.handler.matching.MatchingEngine;
import shop.shportfolio.matching.application.handler.matching.StandardMatchingEngine;
import shop.shportfolio.matching.application.handler.matching.strategy.LimitOrderMatchingStrategy;
import shop.shportfolio.matching.application.handler.matching.strategy.MarketOrderMatchingStrategy;
import shop.shportfolio.matching.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.matching.application.handler.matching.strategy.ReservationOrderMatchingStrategy;
import shop.shportfolio.matching.application.mapper.MatchingDataMapper;
import shop.shportfolio.matching.application.mapper.MatchingDtoMapper;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.kafka.MatchedPublisher;
import shop.shportfolio.matching.application.ports.output.socket.OrderBookSocketClient;
import shop.shportfolio.matching.application.ports.output.socket.OrderBookSender;
import shop.shportfolio.matching.domain.MatchingDomainService;
import shop.shportfolio.matching.domain.MatchingDomainServiceImpl;
import shop.shportfolio.trading.domain.entity.Order;

import java.util.List;

public class TestComponents {

    public List<OrderMatchingStrategy<? extends Order>> strategies;
    public MatchingDomainService matchingDomainService;
    public OrderBookManager orderBookManager;
    public MatchingEngine matchingEngine;

    public TestComponents(OrderBookSocketClient orderBookSocketClient,
                          MatchedPublisher matchedPublisher,
                          ExternalOrderBookMemoryStore externalOrderBookMemoryStore,
                          OrderMemoryStore orderMemoryStore,
                          OrderBookSender orderBookSender) {
        MatchingDtoMapper mapper = new MatchingDtoMapper();
        MatchingDataMapper matchingDataMapper = new MatchingDataMapper();
        matchingDomainService = new MatchingDomainServiceImpl();
        orderBookManager = new OrderBookManager(orderBookSocketClient,
                mapper, externalOrderBookMemoryStore, orderMemoryStore, matchingDataMapper,orderBookSender);
        strategies = List.of(
                new LimitOrderMatchingStrategy(matchingDomainService),
                new MarketOrderMatchingStrategy(matchingDomainService),
                new ReservationOrderMatchingStrategy(matchingDomainService)
        );
        matchingEngine = new StandardMatchingEngine(strategies, orderMemoryStore,
                matchedPublisher, externalOrderBookMemoryStore);
    }

    public MatchingEngine getMatchingEngine() {
        return matchingEngine;
    }

    public OrderBookManager getOrderBookManager() {
        return orderBookManager;
    }
}
