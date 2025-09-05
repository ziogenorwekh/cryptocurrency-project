package shop.shportfolio.matching.application.helper;

import lombok.Getter;

import java.util.Map;

@Getter
public class MarketHardCodingData {

    public static final Map<String, Integer> marketMap = Map.ofEntries(
            Map.entry("KRW-BTC", 10),
            Map.entry("KRW-ETH", 10),
            Map.entry("KRW-XRP", 1),
            Map.entry("KRW-ADA", 1),
            Map.entry("KRW-DOGE", 1),
            Map.entry("KRW-BCH", 1),
            Map.entry("KRW-TRX", 1),
            Map.entry("KRW-XLM", 1),
            Map.entry("KRW-LINK", 1),
            Map.entry("KRW-DOT", 1),
            Map.entry("KRW-SAND", 1),
            Map.entry("KRW-SOL", 1),
            Map.entry("KRW-ATOM", 1),
            Map.entry("KRW-ALGO", 1)
    );
}
