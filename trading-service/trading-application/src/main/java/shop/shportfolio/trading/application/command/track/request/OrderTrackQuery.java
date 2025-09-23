package shop.shportfolio.trading.application.command.track.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrackQuery {

    @NotNull
    private UUID userId;

    @NotNull
    private String marketId;
}
