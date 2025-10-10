package shop.shportfolio.matching.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.matching.application.command.OrderBookTrackQuery;
import shop.shportfolio.matching.application.command.OrderBookTrackResponse;
import shop.shportfolio.matching.application.mapper.MatchingDataMapper;
import shop.shportfolio.matching.application.ports.output.repository.ExternalOrderBookStore;
import shop.shportfolio.matching.application.ports.input.MatchingApplicationService;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;

@Slf4j
@Service
@Validated
public class MatchingApplicationServiceImpl implements MatchingApplicationService {

    private final ExternalOrderBookStore externalOrderBookStore;
    private final MatchingDataMapper matchingDataMapper;

    @Autowired
    public MatchingApplicationServiceImpl(ExternalOrderBookStore externalOrderBookStore,
                                          MatchingDataMapper matchingDataMapper) {
        this.externalOrderBookStore = externalOrderBookStore;
        this.matchingDataMapper = matchingDataMapper;
    }

    @Override
    public OrderBookTrackResponse trackOrderBook(OrderBookTrackQuery query) {
        MatchingOrderBook orderBook = externalOrderBookStore.getOrderBook(query.getMarketId());
        return matchingDataMapper.orderBookToOrderBookTrackResponse(orderBook);
    }
}
