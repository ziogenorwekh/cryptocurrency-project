package shop.shportfolio.trading.application.ports.output.socket;

import shop.shportfolio.trading.application.ports.input.socket.TickerListener;

public interface TickerSocketClient {

    void connect();
    void disconnect();
    void setTickerListener(TickerListener tickerListener);

}
