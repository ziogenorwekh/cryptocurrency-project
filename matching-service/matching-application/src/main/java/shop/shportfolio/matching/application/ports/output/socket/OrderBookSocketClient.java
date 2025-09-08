package shop.shportfolio.matching.application.ports.output.socket;

import shop.shportfolio.matching.application.ports.input.socket.OrderBookListener;

public interface OrderBookSocketClient {
    void connect();
    void disconnect();
    void subscribeMarket(String marketId);
    void setOrderBookListener(OrderBookListener listener);
}