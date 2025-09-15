package shop.shportfolio.marketdata.insight.application.test.factory;

import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;

import java.math.BigDecimal;
import java.util.List;

public class CandleMonthTestFactory {

    public static List<CandleMonthResponseDto> createMockMonthCandles() {
        return List.of(
                new CandleMonthResponseDto("KRW-BTC", "2025-06-30T15:00:00", "2025-07-01T00:00:00",
                        BigDecimal.valueOf(146_250_000), BigDecimal.valueOf(166_969_000), BigDecimal.valueOf(144_250_000),
                        BigDecimal.valueOf(165_431_000), 1_752_490_542_000L, BigDecimal.valueOf(1_252_547_379_286.28458),
                        BigDecimal.valueOf(8145.32324508), "2025-07-01"),
                new CandleMonthResponseDto("KRW-BTC", "2025-05-31T15:00:00", "2025-06-01T00:00:00",
                        BigDecimal.valueOf(148_222_000), BigDecimal.valueOf(151_437_000), BigDecimal.valueOf(137_200_000),
                        BigDecimal.valueOf(146_243_000), 1_751_295_599_569L, BigDecimal.valueOf(1_943_546_525_222.20193),
                        BigDecimal.valueOf(13341.87080267), "2025-06-01"),
                new CandleMonthResponseDto("KRW-BTC", "2025-04-30T15:00:00", "2025-05-01T00:00:00",
                        BigDecimal.valueOf(135_240_000), BigDecimal.valueOf(155_219_000), BigDecimal.valueOf(133_000_000),
                        BigDecimal.valueOf(148_228_000), 1_748_703_598_303L, BigDecimal.valueOf(3_119_365_960_956.80744),
                        BigDecimal.valueOf(21338.21421386), "2025-05-01"),
                new CandleMonthResponseDto("KRW-BTC", "2025-03-31T15:00:00", "2025-04-01T00:00:00",
                        BigDecimal.valueOf(124_219_000), BigDecimal.valueOf(137_760_000), BigDecimal.valueOf(111_850_000),
                        BigDecimal.valueOf(135_319_000), 1_746_025_198_422L, BigDecimal.valueOf(3_062_704_665_996.42641),
                        BigDecimal.valueOf(24642.37233198), "2025-04-01"),
                new CandleMonthResponseDto("KRW-BTC", "2025-02-28T15:00:00", "2025-03-01T00:00:00",
                        BigDecimal.valueOf(121_634_000), BigDecimal.valueOf(143_415_000), BigDecimal.valueOf(114_800_000),
                        BigDecimal.valueOf(124_219_000), 1_743_433_198_701L, BigDecimal.valueOf(3_939_541_931_329.91617),
                        BigDecimal.valueOf(30881.33761547), "2025-03-01"),
                new CandleMonthResponseDto("KRW-BTC", "2025-01-31T15:00:00", "2025-02-01T00:00:00",
                        BigDecimal.valueOf(158_299_000), BigDecimal.valueOf(160_432_000), BigDecimal.valueOf(116_425_000),
                        BigDecimal.valueOf(121_634_000), 1_740_754_799_424L, BigDecimal.valueOf(3_885_223_231_924.95379),
                        BigDecimal.valueOf(27437.69597227), "2025-02-01"),
                new CandleMonthResponseDto("KRW-BTC", "2024-12-31T15:00:00", "2025-01-01T00:00:00",
                        BigDecimal.valueOf(142_074_000), BigDecimal.valueOf(163_460_000), BigDecimal.valueOf(137_300_000),
                        BigDecimal.valueOf(158_298_000), 1_738_335_598_306L, BigDecimal.valueOf(4_669_474_099_892.88287),
                        BigDecimal.valueOf(31072.89872945), "2025-01-01"),
                new CandleMonthResponseDto("KRW-BTC", "2024-11-30T15:00:00", "2024-12-01T00:00:00",
                        BigDecimal.valueOf(133_946_000), BigDecimal.valueOf(157_000_000), BigDecimal.valueOf(110_000_000),
                        BigDecimal.valueOf(142_010_000), 1_735_657_199_144L, BigDecimal.valueOf(6_615_837_652_612.33101),
                        BigDecimal.valueOf(46552.84967332), "2024-12-01"),
                new CandleMonthResponseDto("KRW-BTC", "2024-10-31T15:00:00", "2024-11-01T00:00:00",
                        BigDecimal.valueOf(98_628_000), BigDecimal.valueOf(138_880_000), BigDecimal.valueOf(93_110_000),
                        BigDecimal.valueOf(133_946_000), 1_732_978_798_465L, BigDecimal.valueOf(14_398_064_010_333.32934),
                        BigDecimal.valueOf(123086.33038456), "2024-11-01"),
                new CandleMonthResponseDto("KRW-BTC", "2024-09-30T15:00:00", "2024-10-01T00:00:00",
                        BigDecimal.valueOf(83_897_000), BigDecimal.valueOf(102_102_000), BigDecimal.valueOf(80_596_000),
                        BigDecimal.valueOf(98_654_000), 1_730_386_799_811L, BigDecimal.valueOf(7_756_229_148_056.89038),
                        BigDecimal.valueOf(86408.75531233), "2024-10-01")
        );
    }
}
