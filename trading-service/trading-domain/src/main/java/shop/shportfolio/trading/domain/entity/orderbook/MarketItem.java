package shop.shportfolio.trading.domain.entity.orderbook;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.trading.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.MarketId;

@Getter
public class MarketItem extends BaseEntity<MarketId> {

    private final MarketKoreanName marketKoreanName;
    private final MarketEnglishName marketEnglishName;
    private final MarketWarning marketWarning;
    private final TickPrice tickPrice;
    private final MarketStatus marketStatus;

    @Builder
    public MarketItem(String marketId, MarketKoreanName marketKoreanName, MarketEnglishName marketEnglishName,
                      MarketWarning marketWarning, TickPrice tickPrice,
                      MarketStatus marketStatus) {
        this.tickPrice = tickPrice;
        this.setId(new MarketId(marketId));
        this.marketKoreanName = marketKoreanName;
        this.marketEnglishName = marketEnglishName;
        this.marketWarning = marketWarning;
        this.marketStatus = marketStatus;
    }


    public static MarketItem createMarketItem(String marketId, MarketKoreanName marketKoreanName,
                                              MarketEnglishName marketEnglishName, MarketWarning marketWarning,
                                              TickPrice tickPrice, MarketStatus marketStatus) {
        return new MarketItem(marketId, marketKoreanName, marketEnglishName, marketWarning, tickPrice, marketStatus);
    }

    public Boolean isActive() {
        return marketStatus.equals(MarketStatus.ACTIVE);
    }
    public Boolean isClosed() {
        return marketStatus.equals(MarketStatus.CLOSED);
    }
    public Boolean isPaused() {
        return marketStatus.equals(MarketStatus.PAUSED);
    }
}
