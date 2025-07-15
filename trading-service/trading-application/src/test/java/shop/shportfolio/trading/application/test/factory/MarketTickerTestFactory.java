package shop.shportfolio.trading.application.test.factory;

import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;

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
                        .openingPrice(164_601_000.0)
                        .highPrice(164_797_000.0)
                        .lowPrice(158_819_000.0)
                        .tradePrice(159_951_000.0)
                        .prevClosingPrice(164_600_000.0)
                        .change("FALL")
                        .changePrice(4_649_000.0)
                        .changeRate(0.0282)
                        .signedChangePrice(-4_649_000.0)
                        .signedChangeRate(-0.0282)
                        .tradeVolume(0.00007362)
                        .accTradePrice(124_667_984_352.52446)
                        .accTradePrice24h(204_233_265_475.53428)
                        .accTradeVolume(769.7939945)
                        .accTradeVolume24h(1250.4552369)
                        .highest52WeekPrice(166_969_000.0)
                        .highest52WeekDate("2025-07-15")
                        .lowest52WeekPrice(71_573_000.0)
                        .lowest52WeekDate("2024-08-06")
                        .timestamp(1752560429494L)
                        .build()
        );
    }
}
