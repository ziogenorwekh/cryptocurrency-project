package shop.shportfolio.trading.application.dto.orderbook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookBithumbDto {

    private String market;
    private Long timestamp;
    private Double totalAskSize;
    private Double totalBidSize;
    private List<OrderBookAsksBithumbDto> asks;
    private List<OrderBookBidsBithumbDto> bids;
}
