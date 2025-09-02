package shop.shportfolio.matching.socket.adapter;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import shop.shportfolio.matching.application.command.OrderBookTrackResponse;
import shop.shportfolio.matching.application.ports.input.socket.OrderBookListener;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;

@Component
public class BithumbSocketClientAdapter implements BithumbSocketClient {


    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void subscribeMarket(String marketId) {

    }

    @Override
    public void setOrderBookListener(OrderBookListener listener) {

    }

    @Override
    public void sendOrderBook(OrderBookTrackResponse orderBookTrackResponse) {

    }
}
