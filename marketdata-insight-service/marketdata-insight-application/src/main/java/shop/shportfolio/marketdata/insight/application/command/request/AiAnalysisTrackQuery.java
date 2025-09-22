package shop.shportfolio.marketdata.insight.application.command.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisTrackQuery {

    private String marketId;
    @NotNull(message = "periodType must exist.")
    private PeriodType periodType;

}
