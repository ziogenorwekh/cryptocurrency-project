package shop.shportfolio.trading.infrastructure.database.jpa.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.common.domain.valueobject.MarketEnglishName;
import shop.shportfolio.common.domain.valueobject.MarketKoreanName;
import shop.shportfolio.trading.domain.valueobject.MarketWarning;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.market.MarketItemEntity;

@Component
public class TradingMarketDataDataAccessMapper {

    public MarketItem marketItemEntityToMarketItem(MarketItemEntity marketItem) {
        return MarketItem.builder()
                .marketId(marketItem.getMarketId())
                .marketKoreanName(new MarketKoreanName(marketItem.getMarketKoreanName()))
                .marketEnglishName(new MarketEnglishName(marketItem.getMarketEnglishName()))
                .marketStatus(marketItem.getMarketStatus())
                .marketWarning(marketItem.getMarketWarning() == null ? null : new MarketWarning(marketItem.getMarketWarning()))
                .build();
    }

    public MarketItemEntity marketItemToMarketItemEntity(MarketItem marketItem) {
        return MarketItemEntity.builder()
                .marketId(marketItem.getId().getValue())
                .marketKoreanName(marketItem.getMarketKoreanName().getValue())
                .marketEnglishName(marketItem.getMarketEnglishName().getValue())
                .marketWarning(marketItem.getMarketWarning() == null ? null : marketItem.getMarketWarning().getValue())
                .marketStatus(marketItem.getMarketStatus())
                .build();
    }
}
