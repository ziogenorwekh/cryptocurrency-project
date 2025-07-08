package shop.shportfolio.trading.application.scheduler;

import lombok.Getter;

import java.util.Map;

@Getter
public class MarketHardCodingData {

    public static final Map<String, Integer> marketMap = Map.ofEntries(
            Map.entry("BTC-KRW", 10000),
            Map.entry("ETH-KRW", 1000),
            Map.entry("XRP-KRW", 1),
            Map.entry("ADA-KRW", 1),
            Map.entry("DOGE-KRW", 1),
            Map.entry("BCH-KRW", 500),
            Map.entry("TRX-KRW", 1),
            Map.entry("XLM-KRW", 1),
            Map.entry("LINK-KRW", 10),
            Map.entry("DOT-KRW", 1),
            Map.entry("SAND-KRW", 1),
            Map.entry("SOL-KRW", 100),
            Map.entry("ATOM-KRW", 5),
            Map.entry("ALGO-KRW", 1)
    );
}
