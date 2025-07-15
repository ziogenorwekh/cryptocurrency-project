package shop.shportfolio.trading.application.dto.marketdata.ticker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketTickerRequestDto {

    private String market;
}
