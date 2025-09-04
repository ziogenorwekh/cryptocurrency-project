package shop.shportfolio.matching.application.ports.input.socket;

import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;

public interface OrderBookListener {
    void onOrderBookReceived(OrderBookBithumbDto dto);
}
