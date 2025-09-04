package shop.shportfolio.matching.application.dto.orderbook;

import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookBithumbDto {

    private String market;
    private Long tickPrice;
    private Long timestamp;
    private Double totalAskSize;
    private Double totalBidSize;
    private List<OrderBookAsksBithumbDto> asks;
    private List<OrderBookBidsBithumbDto> bids;

    @Override
    public String toString() {
        return "OrderBookBithumbDto{" +
                "market='" + market + '\'' +
                ", timestamp=" + timestamp +
                ", totalAskSize=" + totalAskSize +
                ", totalBidSize=" + totalBidSize +
                ", asks=" + asks +
                ", bids=" + bids +
                '}';
    }
}
