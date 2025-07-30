package shop.shportfolio.portfolio.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.valueobject.ChangeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AssetChangLogTrackQueryResponse {

    private final UUID userId;
    private final ChangeType changeType;
    private final String marketId;
    private final BigDecimal changeMoney;
    private final String description;
    private final LocalDateTime createdAt;
}
