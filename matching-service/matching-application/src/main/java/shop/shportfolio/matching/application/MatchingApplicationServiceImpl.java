package shop.shportfolio.matching.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.matching.application.command.OrderBookTrackQuery;
import shop.shportfolio.matching.application.command.OrderBookTrackResponse;
import shop.shportfolio.matching.application.mapper.MatchingDataMapper;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.ports.input.MatchingApplicationService;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;

@Slf4j
@Service
@Validated
public class MatchingApplicationServiceImpl implements MatchingApplicationService {

    private final ExternalOrderBookMemoryStore externalOrderBookMemoryStore;
    private final MatchingDataMapper matchingDataMapper;
    private final BithumbSocketClient bithumbSocketClient;

    @Autowired
    public MatchingApplicationServiceImpl(ExternalOrderBookMemoryStore externalOrderBookMemoryStore,
                                          MatchingDataMapper matchingDataMapper,
                                          BithumbSocketClient bithumbSocketClient) {
        this.externalOrderBookMemoryStore = externalOrderBookMemoryStore;
        this.matchingDataMapper = matchingDataMapper;
        this.bithumbSocketClient = bithumbSocketClient;
    }

    @Override
    public OrderBookTrackResponse trackOrderBook(OrderBookTrackQuery query) {
        MatchingOrderBook orderBook = externalOrderBookMemoryStore.getOrderBook(query.getMarketId());
        return matchingDataMapper.orderBookToOrderBookTrackResponse(orderBook);
    }

    @Override
    public void trackSocketOrderBook(OrderBookTrackQuery query) {
        MatchingOrderBook orderBook = externalOrderBookMemoryStore.getOrderBook(query.getMarketId());
        OrderBookTrackResponse orderBookTrackResponse = matchingDataMapper.orderBookToOrderBookTrackResponse(orderBook);
//        bithumbSocketClient.(orderBookTrackResponse);
    }
}
