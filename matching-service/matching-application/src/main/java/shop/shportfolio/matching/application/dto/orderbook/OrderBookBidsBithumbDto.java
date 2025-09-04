package shop.shportfolio.matching.application.dto.orderbook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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

    public String toString() {
        DecimalFormat df = new DecimalFormat("#");
        return "OrderBookBidsBithumbDto{" +
                "bidPrice=" + df.format(bidPrice) +
                ", bidSize=" + bidSize +
                '}';
    }
}
