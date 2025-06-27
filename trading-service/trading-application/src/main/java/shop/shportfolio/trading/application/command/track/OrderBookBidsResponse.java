package shop.shportfolio.trading.application.command.track;

import lombok.Getter;

@Getter
public class OrderBookBidsResponse {
    private final String price;
    private final String quantity;

    public OrderBookBidsResponse(String price, String quantity) {
        this.price = price;
        this.quantity = quantity;
    }
}