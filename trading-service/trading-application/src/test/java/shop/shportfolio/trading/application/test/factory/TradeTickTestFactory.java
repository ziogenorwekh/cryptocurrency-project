package shop.shportfolio.trading.application.test.factory;

import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;

import java.math.BigDecimal;
import java.util.List;

public class TradeTickTestFactory {

    public static List<TradeTickResponseDto> createMockTradeTicks() {
        return List.of(
                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:58")
                        .timestamp(1752560518292L)
                        .tradePrice(new BigDecimal("160008000"))
                        .tradeVolume(new BigDecimal("0.00002699"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4592000"))
                        .askBid("BID")
                        .sequentialId(17525605182920000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:58")
                        .timestamp(1752560518292L)
                        .tradePrice(new BigDecimal("159997000"))
                        .tradeVolume(new BigDecimal("0.0000355"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4603000"))
                        .askBid("BID")
                        .sequentialId(17525605182920000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:58")
                        .timestamp(1752560518004L)
                        .tradePrice(new BigDecimal("159953000"))
                        .tradeVolume(new BigDecimal("0.0013"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4647000"))
                        .askBid("ASK")
                        .sequentialId(17525605180040000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:57")
                        .timestamp(1752560517111L)
                        .tradePrice(new BigDecimal("159952000"))
                        .tradeVolume(new BigDecimal("0.00040784"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4648000"))
                        .askBid("ASK")
                        .sequentialId(17525605171110000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:54")
                        .timestamp(1752560514741L)
                        .tradePrice(new BigDecimal("159997000"))
                        .tradeVolume(new BigDecimal("0.0000625"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4603000"))
                        .askBid("BID")
                        .sequentialId(17525605147410000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:53")
                        .timestamp(1752560513662L)
                        .tradePrice(new BigDecimal("159952000"))
                        .tradeVolume(new BigDecimal("0.01340247"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4648000"))
                        .askBid("ASK")
                        .sequentialId(17525605136620000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:50")
                        .timestamp(1752560510507L)
                        .tradePrice(new BigDecimal("159951000"))
                        .tradeVolume(new BigDecimal("0.00195224"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4649000"))
                        .askBid("ASK")
                        .sequentialId(17525605105070000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:49")
                        .timestamp(1752560509534L)
                        .tradePrice(new BigDecimal("159951000"))
                        .tradeVolume(new BigDecimal("0.00098852"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4649000"))
                        .askBid("ASK")
                        .sequentialId(17525605095340000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:48")
                        .timestamp(1752560508944L)
                        .tradePrice(new BigDecimal("159951000"))
                        .tradeVolume(new BigDecimal("0.0026349"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4649000"))
                        .askBid("ASK")
                        .sequentialId(17525605089440000L)
                        .build(),

                TradeTickResponseDto.builder()
                        .market("KRW-BTC")
                        .tradeDateUtc("2025-07-15")
                        .tradeTimeUtc("06:21:37")
                        .timestamp(1752560497379L)
                        .tradePrice(new BigDecimal("159951000"))
                        .tradeVolume(new BigDecimal("0.0033"))
                        .prevClosingPrice(new BigDecimal("164600000"))
                        .changePrice(new BigDecimal("-4649000"))
                        .askBid("ASK")
                        .sequentialId(17525604973790000L)
                        .build()
        );
    }
}
