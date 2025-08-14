package shop.shportfolio.marketdata.insight.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;

@Getter
public class MarketItem extends AggregateRoot<MarketId> {

    private final MarketKoreanName marketKoreanName;
    private final MarketEnglishName marketEnglishName;
    private MarketStatus marketStatus;

    @Builder
    public MarketItem(MarketId marketId, MarketStatus marketStatus,
                      MarketEnglishName marketEnglishName, MarketKoreanName marketKoreanName) {
        setId(marketId);
        this.marketStatus = marketStatus;
        this.marketEnglishName = marketEnglishName;
        this.marketKoreanName = marketKoreanName;
    }

    public static MarketItem createMarketItem(MarketId marketId, MarketStatus marketStatus,
                                              MarketEnglishName marketEnglishName, MarketKoreanName marketKoreanName) {

        MarketItem marketItem = new MarketItem(marketId, marketStatus,
                marketEnglishName, marketKoreanName);
        return marketItem;
    }

    public Boolean isActive() {
        return marketStatus == MarketStatus.ACTIVE;
    }

}
