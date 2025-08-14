package shop.shportfolio.marketdata.insight.application.command.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MarketCodeTrackResponse {

    private final String marketId;
    private final String marketKoreanName;
    private final String marketEnglishName;
}
