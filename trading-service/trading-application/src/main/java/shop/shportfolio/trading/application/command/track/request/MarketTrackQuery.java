package shop.shportfolio.trading.application.command.track.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketTrackQuery {

    @NotNull(message = "market 아이디는 필수입니다.")
    private String marketId;
}
