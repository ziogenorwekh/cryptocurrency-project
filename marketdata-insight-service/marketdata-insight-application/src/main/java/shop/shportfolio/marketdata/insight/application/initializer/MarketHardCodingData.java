package shop.shportfolio.marketdata.insight.application.initializer;

import lombok.Getter;

import java.util.Map;

@Getter
public class MarketHardCodingData {

    public static final Map<String, Integer> marketMap = Map.ofEntries(
            Map.entry("KRW-BTC", 10000),
            Map.entry("KRW-ETH", 1000),
            Map.entry("KRW-XRP", 1),
            Map.entry("KRW-ADA", 1),
            Map.entry("KRW-DOGE", 1),
            Map.entry("KRW-BCH", 500),
//            Map.entry("KRW-TRX", 1),
            Map.entry("KRW-XLM", 1),
            Map.entry("KRW-LINK", 10),
//            Map.entry("KRW-DOT", 1),
//            Map.entry("KRW-SAND", 1),
            Map.entry("KRW-SOL", 100)
//            Map.entry("KRW-ATOM", 5),
//            Map.entry("KRW-ALGO", 1)
    );
}
