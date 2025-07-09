package shop.shportfolio.trading.application.dto.marketdata;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CandleRequestDto {
    private final String market;
    private final String to;
    private final Integer count;
}
