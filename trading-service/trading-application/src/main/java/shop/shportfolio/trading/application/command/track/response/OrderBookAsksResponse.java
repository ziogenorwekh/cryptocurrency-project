package shop.shportfolio.trading.application.command.track.response;

import lombok.Getter;

@Getter
public class OrderBookAsksResponse {
    private final String price;
    private final String quantity;

    public OrderBookAsksResponse(String price, String quantity) {
        this.price = price;
        this.quantity = quantity;
    }
}