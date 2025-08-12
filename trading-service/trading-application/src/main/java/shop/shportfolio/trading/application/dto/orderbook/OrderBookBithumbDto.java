package shop.shportfolio.trading.application.dto.orderbook;

import lombok.*;

import java.util.List;

@Builder
@Data
@ToString
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
