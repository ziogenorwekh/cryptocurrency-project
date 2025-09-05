package shop.shportfolio.matching.application.command;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderBookTrackResponse {

    private final String marketId;
    private final String totalAskSize;
    private final String totalBidSize;
    private final List<OrderBookBidsResponse> orderBookBidsResponse;
    private final List<OrderBookAsksResponse> orderBookAsksResponse;

    @Builder
    public OrderBookTrackResponse(String marketId, String totalAskSize, String totalBidSize, List<OrderBookBidsResponse> orderBookBidsResponse,
                                  List<OrderBookAsksResponse> orderBookAsksResponse) {
        this.marketId = marketId;
        this.totalAskSize = totalAskSize;
        this.totalBidSize = totalBidSize;
        this.orderBookBidsResponse = orderBookBidsResponse;
        this.orderBookAsksResponse = orderBookAsksResponse;
    }
}
