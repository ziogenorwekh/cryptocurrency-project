package shop.shportfolio.trading.application.dto.orderbook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 매수 리스트
 */
public class OrderBookBidsBithumbDto {
    /**
     * bidPrice : 매수 호가
     */
    private Double bidPrice;
    private Double bidSize;
}
