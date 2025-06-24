package shop.shportfolio.trading.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookDto {

    private String market;
    private Long timestamp;
    private Double totalAskSize;
    private Double totalBidSize;
    private List<OrderBookAsksDto> asks;
    private List<OrderBookBidsDto> bids;

}
