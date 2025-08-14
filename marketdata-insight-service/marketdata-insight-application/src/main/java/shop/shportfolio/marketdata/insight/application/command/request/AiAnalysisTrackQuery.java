package shop.shportfolio.marketdata.insight.application.command.request;

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
    private LocalDateTime periodEnd;
    private LocalDateTime periodStart;
    private PeriodType periodType;

}
