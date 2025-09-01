package shop.shportfolio.matching.application.ports.input;

import shop.shportfolio.matching.application.command.OrderBookTrackQuery;
import shop.shportfolio.matching.application.command.OrderBookTrackResponse;

public interface MatchingApplicationService {

    OrderBookTrackResponse trackOrderBook(OrderBookTrackQuery query);

    void trackSocketOrderBook(OrderBookTrackQuery query);
}
