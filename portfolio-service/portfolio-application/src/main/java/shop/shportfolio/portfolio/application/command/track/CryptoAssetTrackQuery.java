package shop.shportfolio.portfolio.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoAssetTrackQuery {

    private UUID tokenUserId;
    private String marketId;
}
