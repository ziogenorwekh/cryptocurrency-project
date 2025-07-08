package shop.shportfolio.trading.application.dto.marketdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MarketItemBithumbDto {
    private String marketId;
    private String koreanName;
    private String englishName;
}
