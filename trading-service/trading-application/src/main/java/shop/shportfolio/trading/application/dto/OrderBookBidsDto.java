package shop.shportfolio.trading.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 매수 리스트
 */
public class OrderBookBidsDto {

    /**
     * bidPrice : 매수 호가
     */
    private Double bidPrice;
    private Double bidSize;

}
