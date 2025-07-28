package shop.shportfolio.portfolio.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TotalAssetValueTrackQuery {
    private UUID portfolioId;
    private UUID userId;
}
