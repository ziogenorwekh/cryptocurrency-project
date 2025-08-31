package shop.shportfolio.matching.application.command;

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