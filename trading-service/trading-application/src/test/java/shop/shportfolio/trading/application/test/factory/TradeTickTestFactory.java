package shop.shportfolio.trading.application.test.factory;

import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;

import java.util.List;

public class TradeTickTestFactory {

    public static List<TradeTickResponseDto> createMockTradeTicks() {
        return List.of(
                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:58")
                        .timestamp(1752560518292L)
                        .tradePrice(160_008_000.0)
                        .tradeVolume(0.00002699)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_592_000.0)
                        .askBid("BID")
                        .sequentialId(17525605182920000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:58")
                        .timestamp(1752560518292L)
                        .tradePrice(159_997_000.0)
                        .tradeVolume(0.0000355)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_603_000.0)
                        .askBid("BID")
                        .sequentialId(17525605182920000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:58")
                        .timestamp(1752560518004L)
                        .tradePrice(159_953_000.0)
                        .tradeVolume(0.0013)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_647_000.0)
                        .askBid("ASK")
                        .sequentialId(17525605180040000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:57")
                        .timestamp(1752560517111L)
                        .tradePrice(159_952_000.0)
                        .tradeVolume(0.00040784)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_648_000.0)
                        .askBid("ASK")
                        .sequentialId(17525605171110000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:54")
                        .timestamp(1752560514741L)
                        .tradePrice(159_997_000.0)
                        .tradeVolume(0.0000625)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_603_000.0)
                        .askBid("BID")
                        .sequentialId(17525605147410000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:53")
                        .timestamp(1752560513662L)
                        .tradePrice(159_952_000.0)
                        .tradeVolume(0.01340247)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_648_000.0)
                        .askBid("ASK")
                        .sequentialId(17525605136620000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:50")
                        .timestamp(1752560510507L)
                        .tradePrice(159_951_000.0)
                        .tradeVolume(0.00195224)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_649_000.0)
                        .askBid("ASK")
                        .sequentialId(17525605105070000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:49")
                        .timestamp(1752560509534L)
                        .tradePrice(159_951_000.0)
                        .tradeVolume(0.00098852)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_649_000.0)
                        .askBid("ASK")
                        .sequentialId(17525605095340000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:48")
                        .timestamp(1752560508944L)
                        .tradePrice(159_951_000.0)
                        .tradeVolume(0.0026349)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_649_000.0)
                        .askBid("ASK")
                        .sequentialId(17525605089440000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:37")
                        .timestamp(1752560497379L)
                        .tradePrice(159_951_000.0)
                        .tradeVolume(0.0033)
                        .prevClosingPrice(164_600_000.0)
                        .changePrice(-4_649_000.0)
                        .askBid("ASK")
                        .sequentialId(17525604973790000L)
                        .build()
        );
    }
}
