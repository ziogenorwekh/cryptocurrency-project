package shop.shportfolio.trading.application.test.factory;

import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;

import java.math.BigDecimal;
import java.util.List;

public class MarketTickerTestFactory {

    public static List<MarketTickerResponseDto> createMockTicker() {
        return List.of(
                MarketTickerResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDate("20250715")
                        .tradeTime("062028")
                        .tradeDateKst("20250715")
                        .tradeTimeKst("152028")
                        .tradeTimestamp(1752592828620L)
                        .openingPrice(BigDecimal.valueOf(164_601_000.0))
                        .highPrice(BigDecimal.valueOf(164_797_000.0))
                        .lowPrice(BigDecimal.valueOf(158_819_000.0))
                        .tradePrice(BigDecimal.valueOf(159_951_000.0))
                        .prevClosingPrice(BigDecimal.valueOf(164_600_000.0))
                        .change("FALL")
                        .changePrice(BigDecimal.valueOf(4_649_000.0))
                        .changeRate(BigDecimal.valueOf(0.0282))
                        .signedChangePrice(BigDecimal.valueOf(-4_649_000.0))
                        .signedChangeRate(BigDecimal.valueOf(-0.0282))
                        .tradeVolume(BigDecimal.valueOf(0.00007362))
                        .accTradePrice(BigDecimal.valueOf(124_667_984_352.52446))
                        .accTradePrice24h(BigDecimal.valueOf(204_233_265_475.53428))
                        .accTradeVolume(BigDecimal.valueOf(769.7939945))
                        .accTradeVolume24h(BigDecimal.valueOf(1250.4552369))
                        .highest52WeekPrice(BigDecimal.valueOf(166_969_000.0))
                        .highest52WeekDate("2025-07-15")
                        .lowest52WeekPrice(BigDecimal.valueOf(71_573_000.0))
                        .lowest52WeekDate("2024-08-06")
                        .timestamp(1752560429494L)
                        .build()
        );
    }
}
