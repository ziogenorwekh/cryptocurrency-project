package shop.shportfolio.trading.application.ports.output.socket;

import shop.shportfolio.trading.application.ports.input.socket.TradeListener;

public interface TradeSocketClient {

    void connect();
    void disconnect();
    void setTradeListener(TradeListener tradeListener);
}
