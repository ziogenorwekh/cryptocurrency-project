package shop.shportfolio.trading.application.dto.orderbook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 매도 리스트
 */
public class OrderBookAsksDto {
    /**
     * askPrice : 매도 호가
     */
    private Double askPrice;
    private Double askSize;

}
