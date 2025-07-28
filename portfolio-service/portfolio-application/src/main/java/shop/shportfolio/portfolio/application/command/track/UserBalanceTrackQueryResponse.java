package shop.shportfolio.portfolio.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.AssetCode;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserBalanceTrackQueryResponse {
    private final UUID userId;
    private final AssetCode assetCode;
    private final BigDecimal money;
}
