package shop.shportfolio.matching.application.test.helper;

import shop.shportfolio.matching.application.handler.OrderBookManager;
import shop.shportfolio.matching.application.handler.matching.MatchingEngine;
import shop.shportfolio.matching.application.handler.matching.StandardMatchingEngine;
import shop.shportfolio.matching.application.handler.matching.strategy.LimitOrderMatchingStrategy;
import shop.shportfolio.matching.application.handler.matching.strategy.MarketOrderMatchingStrategy;
import shop.shportfolio.matching.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.matching.application.handler.matching.strategy.ReservationOrderMatchingStrategy;
import shop.shportfolio.matching.application.mapper.MatchingDtoMapper;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.kafka.MatchedKafkaPublisher;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;
import shop.shportfolio.matching.domain.MatchingDomainService;
import shop.shportfolio.matching.domain.MatchingDomainServiceImpl;
import shop.shportfolio.trading.domain.entity.Order;

import java.util.List;

public class TestComponents {

    public List<OrderMatchingStrategy<? extends Order>> strategies;
    public MatchingDomainService matchingDomainService;
    public OrderBookManager orderBookManager;
    public MatchingEngine matchingEngine;

    public TestComponents(BithumbSocketClient bithumbSocketClient,
                          MatchedKafkaPublisher matchedKafkaPublisher,
                          ExternalOrderBookMemoryStore externalOrderBookMemoryStore,
                          OrderMemoryStore orderMemoryStore) {
        MatchingDtoMapper mapper = new MatchingDtoMapper();
        matchingDomainService = new MatchingDomainServiceImpl();
        orderBookManager = new OrderBookManager(bithumbSocketClient, mapper, externalOrderBookMemoryStore, orderMemoryStore);
        strategies = List.of(
                new LimitOrderMatchingStrategy(matchingDomainService),
                new MarketOrderMatchingStrategy(matchingDomainService),
                new ReservationOrderMatchingStrategy(matchingDomainService)
        );
        matchingEngine = new StandardMatchingEngine(strategies, orderBookManager, orderMemoryStore,
                matchedKafkaPublisher, externalOrderBookMemoryStore);
    }

    public MatchingEngine getMatchingEngine() {
        return matchingEngine;
    }

    public OrderBookManager getOrderBookManager() {
        return orderBookManager;
    }
}
