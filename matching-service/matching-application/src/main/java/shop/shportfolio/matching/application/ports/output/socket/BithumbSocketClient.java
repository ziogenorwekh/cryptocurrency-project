package shop.shportfolio.matching.application.ports.output.socket;

import shop.shportfolio.matching.application.command.OrderBookTrackResponse;
import shop.shportfolio.matching.application.ports.input.socket.OrderBookListener;

import java.util.List;
import java.util.Set;

public interface BithumbSocketClient {
    void connect();
    void disconnect();
    void subscribeMarket(String marketId);
    void setOrderBookListener(OrderBookListener listener);
}