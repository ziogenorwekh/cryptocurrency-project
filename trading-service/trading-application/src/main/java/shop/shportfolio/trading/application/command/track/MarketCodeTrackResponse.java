package shop.shportfolio.trading.application.command.track;

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
