package shop.shportfolio.marketdata.insight.application.test.factory;

import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;

import java.math.BigDecimal;
import java.util.List;

public class CandleMinuteTestFactory {

    public static List<CandleMinuteResponseDto> createMockMinuteCandles() {
        return List.of(
                new CandleMinuteResponseDto("KRW-BTC", "2025-09-24T01:30:00", "2025-09-24T10:30:00",
                        BigDecimal.valueOf(165_596_000), BigDecimal.valueOf(165_596_000), BigDecimal.valueOf(165_498_000),
                        BigDecimal.valueOf(165_498_000), 1_752_490_242_000L, BigDecimal.valueOf(63_584_776.44121),
                        BigDecimal.valueOf(0.3840855), 30),
                new CandleMinuteResponseDto("KRW-BTC", "2025-09-24T01:00:00", "2025-09-24T10:00:00",
                        BigDecimal.valueOf(165_654_000), BigDecimal.valueOf(165_662_000), BigDecimal.valueOf(165_600_000),
                        BigDecimal.valueOf(165_600_000), 1_752_490_199_424L, BigDecimal.valueOf(36_811_676.33326),
                        BigDecimal.valueOf(0.22224931), 30),
                new CandleMinuteResponseDto("KRW-BTC", "2025-09-24T00:30:00", "2025-09-24T09:30:00",
                        BigDecimal.valueOf(165_654_000), BigDecimal.valueOf(165_662_000), BigDecimal.valueOf(165_654_000),
                        BigDecimal.valueOf(165_662_000), 1_752_490_138_643L, BigDecimal.valueOf(29_423_808.60295),
                        BigDecimal.valueOf(0.17761779), 30),
                new CandleMinuteResponseDto("KRW-BTC", "2025-09-24T00:00:00", "2025-09-24T09:00:00",
                        BigDecimal.valueOf(165_663_000), BigDecimal.valueOf(165_663_000), BigDecimal.valueOf(165_650_000),
                        BigDecimal.valueOf(165_662_000), 1_752_490_076_270L, BigDecimal.valueOf(124_759_849.69071),
                        BigDecimal.valueOf(0.75310012), 30),
                new CandleMinuteResponseDto("KRW-BTC", "2025-09-23T23:30:00", "2025-09-24T08:30:00",
                        BigDecimal.valueOf(165_663_000), BigDecimal.valueOf(165_663_000), BigDecimal.valueOf(165_661_000),
                        BigDecimal.valueOf(165_663_000), 1_752_490_018_375L, BigDecimal.valueOf(48_933_380.31737),
                        BigDecimal.valueOf(0.29537965), 30)
        );
    }


    public static List<CandleMinuteResponseDto> candleMinuteResponseDtoOneHours() {
        return List.of(
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T09:00:00", "2025-08-15T18:00:00",
                        BigDecimal.valueOf(164_887_000), BigDecimal.valueOf(164_890_000), BigDecimal.valueOf(164_830_000),
                        BigDecimal.valueOf(164_889_000), 1_755_248_458_471L, BigDecimal.valueOf(15_276_255.41986),
                        BigDecimal.valueOf(0.09264621), 60),
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T08:00:00", "2025-08-15T17:00:00",
                        BigDecimal.valueOf(164_999_000), BigDecimal.valueOf(165_132_000), BigDecimal.valueOf(164_830_000),
                        BigDecimal.valueOf(164_890_000), 1_755_248_389_181L, BigDecimal.valueOf(2_248_105_972.13252),
                        BigDecimal.valueOf(13.62305493), 60),
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T07:00:00", "2025-08-15T16:00:00",
                        BigDecimal.valueOf(165_000_000), BigDecimal.valueOf(165_104_000), BigDecimal.valueOf(164_811_000),
                        BigDecimal.valueOf(165_000_000), 1_755_244_797_552L, BigDecimal.valueOf(2_018_498_257.36713),
                        BigDecimal.valueOf(12.2383702), 60),
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T06:00:00", "2025-08-15T15:00:00",
                        BigDecimal.valueOf(165_054_000), BigDecimal.valueOf(165_266_000), BigDecimal.valueOf(164_900_000),
                        BigDecimal.valueOf(164_976_000), 1_755_241_199_691L, BigDecimal.valueOf(2_455_269_403.42412),
                        BigDecimal.valueOf(14.86877452), 60),
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T05:00:00", "2025-08-15T14:00:00",
                        BigDecimal.valueOf(165_273_000), BigDecimal.valueOf(165_324_000), BigDecimal.valueOf(164_667_000),
                        BigDecimal.valueOf(165_027_000), 1_755_237_599_274L, BigDecimal.valueOf(3_266_520_528.59915),
                        BigDecimal.valueOf(19.80042197), 60),
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T04:00:00", "2025-08-15T13:00:00",
                        BigDecimal.valueOf(165_533_000), BigDecimal.valueOf(165_785_000), BigDecimal.valueOf(165_245_000),
                        BigDecimal.valueOf(165_245_000), 1_755_233_995_284L, BigDecimal.valueOf(4_244_611_330.09431),
                        BigDecimal.valueOf(25.63452828), 60),
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T03:00:00", "2025-08-15T12:00:00",
                        BigDecimal.valueOf(165_465_000), BigDecimal.valueOf(165_636_000), BigDecimal.valueOf(165_390_000),
                        BigDecimal.valueOf(165_493_000), 1_755_230_398_284L, BigDecimal.valueOf(2_070_416_121.19302),
                        BigDecimal.valueOf(12.50589157), 60),
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T02:00:00", "2025-08-15T11:00:00",
                        BigDecimal.valueOf(165_485_000), BigDecimal.valueOf(165_573_000), BigDecimal.valueOf(165_216_000),
                        BigDecimal.valueOf(165_463_000), 1_755_226_799_229L, BigDecimal.valueOf(11_227_910_168.00196),
                        BigDecimal.valueOf(67.87062462), 60),
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T01:00:00", "2025-08-15T10:00:00",
                        BigDecimal.valueOf(164_765_000), BigDecimal.valueOf(165_487_000), BigDecimal.valueOf(164_576_000),
                        BigDecimal.valueOf(165_485_000), 1_755_223_199_737L, BigDecimal.valueOf(3_421_285_897.26296),
                        BigDecimal.valueOf(20.73530134), 60),
                new CandleMinuteResponseDto("KRW-BTC", "2025-08-15T00:00:00", "2025-08-15T09:00:00",
                        BigDecimal.valueOf(164_771_000), BigDecimal.valueOf(165_037_000), BigDecimal.valueOf(164_438_000),
                        BigDecimal.valueOf(164_765_000), 1_755_219_599_527L, BigDecimal.valueOf(6_104_919_803.52629),
                        BigDecimal.valueOf(37.04870984), 60)
        );
    }
}
