package shop.shportfolio.trading.application.command.track.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TickerTrackQuery {

    @NotNull(message = "마켓 아이디는 null이어서는 안됩니다.")
    private String marketId;
}
