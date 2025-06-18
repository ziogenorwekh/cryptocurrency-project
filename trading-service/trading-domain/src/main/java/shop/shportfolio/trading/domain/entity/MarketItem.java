package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.trading.domain.valueobject.MarketEnglishName;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.trading.domain.valueobject.MarketKoreanName;
import shop.shportfolio.trading.domain.valueobject.MarketWarning;

@Getter
public class MarketItem extends BaseEntity<MarketId> {

    private final MarketKoreanName marketKoreanName;
    private final MarketEnglishName marketEnglishName;
    private final MarketWarning marketWarning;

    public MarketItem(String marketId, MarketKoreanName marketKoreanName, MarketEnglishName marketEnglishName,
                      MarketWarning marketWarning) {
        this.setId(new MarketId(marketId));
        this.marketKoreanName = marketKoreanName;
        this.marketEnglishName = marketEnglishName;
        this.marketWarning = marketWarning;
    }
}
