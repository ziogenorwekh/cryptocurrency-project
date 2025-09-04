package shop.shportfolio.matching.application.dto.orderbook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 매도 리스트
 */
public class OrderBookAsksBithumbDto {
    /**
     * askPrice : 매도 호가
     */
    private Double askPrice;
    private Double askSize;


    @Override
    public String toString() {
        return "OrderBookAsksBithumbDto{" +
                "askPrice=" + BigDecimal.valueOf(askPrice) +
                ", askSize=" + BigDecimal.valueOf(askSize) +
                '}';
    }
}
