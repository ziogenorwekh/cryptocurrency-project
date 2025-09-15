package shop.shportfolio.marketdata.insight.application.test.factory;

import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;

import java.math.BigDecimal;
import java.util.List;

public class CandleDayTestFactory {

    public static List<CandleDayResponseDto> createMockDayCandles() {
        return List.of(
                new CandleDayResponseDto("KRW-BTC", "2025-07-13T15:00:00", "2025-07-14T00:00:00",
                        BigDecimal.valueOf(160_720_000), BigDecimal.valueOf(166_969_000), BigDecimal.valueOf(160_599_000),
                        BigDecimal.valueOf(165_480_000), BigDecimal.valueOf(172_094_071_620.496), BigDecimal.valueOf(1049.277984),
                        BigDecimal.valueOf(160_821_000), BigDecimal.valueOf(4_659_000), BigDecimal.valueOf(0.0289700972)),
                new CandleDayResponseDto("KRW-BTC", "2025-07-12T15:00:00", "2025-07-13T00:00:00",
                        BigDecimal.valueOf(159_588_000), BigDecimal.valueOf(161_329_000), BigDecimal.valueOf(159_122_000),
                        BigDecimal.valueOf(160_821_000), BigDecimal.valueOf(81_107_901_601.77477), BigDecimal.valueOf(506.65879508),
                        BigDecimal.valueOf(159_588_000), BigDecimal.valueOf(1_233_000), BigDecimal.valueOf(0.0077261448)),
                new CandleDayResponseDto("KRW-BTC", "2025-07-11T15:00:00", "2025-07-12T00:00:00",
                        BigDecimal.valueOf(158_770_000), BigDecimal.valueOf(159_939_000), BigDecimal.valueOf(156_800_000),
                        BigDecimal.valueOf(159_588_000), BigDecimal.valueOf(114_563_940_463.77004), BigDecimal.valueOf(722.95671363),
                        BigDecimal.valueOf(158_771_000), BigDecimal.valueOf(817_000), BigDecimal.valueOf(0.005145776)),
                new CandleDayResponseDto("KRW-BTC", "2025-07-10T15:00:00", "2025-07-11T00:00:00",
                        BigDecimal.valueOf(151_154_000), BigDecimal.valueOf(160_762_000), BigDecimal.valueOf(150_980_000),
                        BigDecimal.valueOf(158_771_000), BigDecimal.valueOf(266_633_218_173.7261), BigDecimal.valueOf(1695.5655735),
                        BigDecimal.valueOf(151_170_000), BigDecimal.valueOf(7_601_000), BigDecimal.valueOf(0.0502811404)),
                new CandleDayResponseDto("KRW-BTC", "2025-07-09T15:00:00", "2025-07-10T00:00:00",
                        BigDecimal.valueOf(148_388_000), BigDecimal.valueOf(151_528_000), BigDecimal.valueOf(148_286_000),
                        BigDecimal.valueOf(151_170_000), BigDecimal.valueOf(111_835_571_572.92026), BigDecimal.valueOf(742.4447227),
                        BigDecimal.valueOf(148_391_000), BigDecimal.valueOf(2_779_000), BigDecimal.valueOf(0.0187275509)),
                new CandleDayResponseDto("KRW-BTC", "2025-07-08T15:00:00", "2025-07-09T00:00:00",
                        BigDecimal.valueOf(148_002_000), BigDecimal.valueOf(149_300_000), BigDecimal.valueOf(147_830_000),
                        BigDecimal.valueOf(148_391_000), BigDecimal.valueOf(56_343_551_143.24087), BigDecimal.valueOf(379.37823165),
                        BigDecimal.valueOf(147_976_000), BigDecimal.valueOf(415_000), BigDecimal.valueOf(0.0028045088)),
                new CandleDayResponseDto("KRW-BTC", "2025-07-07T15:00:00", "2025-07-08T00:00:00",
                        BigDecimal.valueOf(147_598_000), BigDecimal.valueOf(148_834_000), BigDecimal.valueOf(147_047_000),
                        BigDecimal.valueOf(147_976_000), BigDecimal.valueOf(48_519_960_233.09993), BigDecimal.valueOf(328.08791163),
                        BigDecimal.valueOf(147_606_000), BigDecimal.valueOf(370_000), BigDecimal.valueOf(0.0025066732)),
                new CandleDayResponseDto("KRW-BTC", "2025-07-06T15:00:00", "2025-07-07T00:00:00",
                        BigDecimal.valueOf(148_289_000), BigDecimal.valueOf(149_214_000), BigDecimal.valueOf(147_300_000),
                        BigDecimal.valueOf(147_606_000), BigDecimal.valueOf(58_171_638_759.80212), BigDecimal.valueOf(392.40466344),
                        BigDecimal.valueOf(148_298_000), BigDecimal.valueOf(-692_000), BigDecimal.valueOf(-0.0046662801)),
                new CandleDayResponseDto("KRW-BTC", "2025-07-05T15:00:00", "2025-07-06T00:00:00",
                        BigDecimal.valueOf(147_953_000), BigDecimal.valueOf(148_500_000), BigDecimal.valueOf(147_396_000),
                        BigDecimal.valueOf(148_298_000), BigDecimal.valueOf(28_229_787_027.15458), BigDecimal.valueOf(190.86522349),
                        BigDecimal.valueOf(147_953_000), BigDecimal.valueOf(345_000), BigDecimal.valueOf(0.0023318216)),
                new CandleDayResponseDto("KRW-BTC", "2025-07-04T15:00:00", "2025-07-05T00:00:00",
                        BigDecimal.valueOf(147_141_000), BigDecimal.valueOf(148_190_000), BigDecimal.valueOf(146_800_000),
                        BigDecimal.valueOf(147_953_000), BigDecimal.valueOf(33_793_104_862.64385), BigDecimal.valueOf(229.00078342),
                        BigDecimal.valueOf(147_141_000), BigDecimal.valueOf(812_000), BigDecimal.valueOf(0.0055185163))
        );
    }
}
