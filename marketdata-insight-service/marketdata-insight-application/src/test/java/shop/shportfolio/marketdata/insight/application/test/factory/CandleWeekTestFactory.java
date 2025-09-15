package shop.shportfolio.marketdata.insight.application.test.factory;

import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;

import java.math.BigDecimal;
import java.util.List;

public class CandleWeekTestFactory {

    public static List<CandleWeekResponseDto> createMockWeekCandles() {
        return List.of(
                new CandleWeekResponseDto("KRW-BTC", "2025-07-13T15:00:00", "2025-07-14T00:00:00",
                        BigDecimal.valueOf(160_720_000), BigDecimal.valueOf(166_969_000), BigDecimal.valueOf(160_599_000),
                        BigDecimal.valueOf(165_433_000), 1_752_490_498_000L, BigDecimal.valueOf(172_193_209_893.55711),
                        BigDecimal.valueOf(1049.87716674), "2025-07-14"),
                new CandleWeekResponseDto("KRW-BTC", "2025-07-06T15:00:00", "2025-07-07T00:00:00",
                        BigDecimal.valueOf(148_289_000), BigDecimal.valueOf(161_329_000), BigDecimal.valueOf(147_047_000),
                        BigDecimal.valueOf(160_821_000), 1_752_418_799_940L, BigDecimal.valueOf(737_175_781_948.33409),
                        BigDecimal.valueOf(4767.49661163), "2025-07-07"),
                new CandleWeekResponseDto("KRW-BTC", "2025-06-29T15:00:00", "2025-06-30T00:00:00",
                        BigDecimal.valueOf(147_950_000), BigDecimal.valueOf(149_890_000), BigDecimal.valueOf(144_250_000),
                        BigDecimal.valueOf(148_298_000), 1_751_813_997_751L, BigDecimal.valueOf(391_916_793_810.94832),
                        BigDecimal.valueOf(2658.68689414), "2025-06-30"),
                new CandleWeekResponseDto("KRW-BTC", "2025-06-22T15:00:00", "2025-06-23T00:00:00",
                        BigDecimal.valueOf(138_838_000), BigDecimal.valueOf(148_497_000), BigDecimal.valueOf(137_200_000),
                        BigDecimal.valueOf(147_950_000), 1_751_209_197_707L, BigDecimal.valueOf(474_134_103_763.15861),
                        BigDecimal.valueOf(3278.17032372), "2025-06-23"),
                new CandleWeekResponseDto("KRW-BTC", "2025-06-15T15:00:00", "2025-06-16T00:00:00",
                        BigDecimal.valueOf(146_328_000), BigDecimal.valueOf(149_312_000), BigDecimal.valueOf(138_488_000),
                        BigDecimal.valueOf(138_838_000), 1_750_604_399_327L, BigDecimal.valueOf(495_876_847_191.7253),
                        BigDecimal.valueOf(3430.10513423), "2025-06-16"),
                new CandleWeekResponseDto("KRW-BTC", "2025-06-08T15:00:00", "2025-06-09T00:00:00",
                        BigDecimal.valueOf(146_110_000), BigDecimal.valueOf(151_437_000), BigDecimal.valueOf(143_000_000),
                        BigDecimal.valueOf(146_328_000), 1_749_999_598_123L, BigDecimal.valueOf(493_779_986_502.02922),
                        BigDecimal.valueOf(3346.07520838), "2025-06-09"),
                new CandleWeekResponseDto("KRW-BTC", "2025-06-01T15:00:00", "2025-06-02T00:00:00",
                        BigDecimal.valueOf(147_884_000), BigDecimal.valueOf(149_351_000), BigDecimal.valueOf(140_470_000),
                        BigDecimal.valueOf(146_117_000), 1_749_394_799_017L, BigDecimal.valueOf(396_120_661_092.84623),
                        BigDecimal.valueOf(2720.72887291), "2025-06-02"),
                new CandleWeekResponseDto("KRW-BTC", "2025-05-25T15:00:00", "2025-05-26T00:00:00",
                        BigDecimal.valueOf(150_247_000), BigDecimal.valueOf(153_127_000), BigDecimal.valueOf(146_160_000),
                        BigDecimal.valueOf(147_916_000), 1_748_789_998_314L, BigDecimal.valueOf(578_750_344_264.25208),
                        BigDecimal.valueOf(3861.40715248), "2025-05-26"),
                new CandleWeekResponseDto("KRW-BTC", "2025-05-18T15:00:00", "2025-05-19T00:00:00",
                        BigDecimal.valueOf(148_996_000), BigDecimal.valueOf(155_219_000), BigDecimal.valueOf(145_470_000),
                        BigDecimal.valueOf(150_248_000), 1_748_185_199_961L, BigDecimal.valueOf(982_294_599_548.17374),
                        BigDecimal.valueOf(6508.44000968), "2025-05-19"),
                new CandleWeekResponseDto("KRW-BTC", "2025-05-11T15:00:00", "2025-05-12T00:00:00",
                        BigDecimal.valueOf(145_584_000), BigDecimal.valueOf(149_572_000), BigDecimal.valueOf(142_412_000),
                        BigDecimal.valueOf(148_996_000), 1_747_580_399_654L, BigDecimal.valueOf(632_447_367_356.41889),
                        BigDecimal.valueOf(4338.28047858), "2025-05-12")
        );
    }
}
