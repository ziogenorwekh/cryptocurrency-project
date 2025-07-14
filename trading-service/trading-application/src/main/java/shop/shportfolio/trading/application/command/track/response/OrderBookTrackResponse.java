package shop.shportfolio.trading.application.command.track.response;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderBookTrackResponse {

    private final String marketId;
    private final List<OrderBookBidsResponse> orderBookBidsResponse;
    private final List<OrderBookAsksResponse> orderBookAsksResponse;

    public OrderBookTrackResponse(String marketId, List<OrderBookBidsResponse> orderBookBidsResponse,
                                  List<OrderBookAsksResponse> orderBookAsksResponse) {
        this.marketId = marketId;
        this.orderBookBidsResponse = orderBookBidsResponse;
        this.orderBookAsksResponse = orderBookAsksResponse;
    }
}
