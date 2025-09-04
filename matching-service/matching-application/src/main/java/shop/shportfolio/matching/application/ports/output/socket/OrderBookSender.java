package shop.shportfolio.matching.application.ports.output.socket;

import shop.shportfolio.matching.application.command.OrderBookTrackResponse;

public interface OrderBookSender {

    void send(OrderBookTrackResponse orderBookTrackResponse);
}
