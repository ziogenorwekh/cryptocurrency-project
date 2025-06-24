package shop.shportfolio.trading.application.support;

public final class RedisKeyPrefix {
    private RedisKeyPrefix() {}  // 인스턴스 생성 금지

    public static final String ORDERBOOK_PREFIX = "orderbook";
    public static final String TICKER_PREFIX = "ticker";
    public static final String TRADE_PREFIX = "trade";

    public static String orderbook(String market) {
        return ORDERBOOK_PREFIX + ":" + market;
    }

    public static String ticker(String market) {
        return TICKER_PREFIX + ":" + market;
    }

    public static String trade(String market) {
        return TRADE_PREFIX + ":" + market;
    }
}