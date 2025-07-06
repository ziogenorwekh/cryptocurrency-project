package shop.shportfolio.trading.application.support;

public final class RedisKeyPrefix {
    private RedisKeyPrefix() {}  // 인스턴스 생성 금지

    public static final String ORDERBOOK_PREFIX = "orderbook";
    public static final String TICKER_PREFIX = "ticker";
    public static final String TRADE_PREFIX = "trade";
    public static final String MARKET_PREFIX = "market";
    public static final String LIMIT_PREFIX = "limit";
    public static final String RESERVED_PREFIX = "reservation";


    public static String orderBook(String market) {
        return ORDERBOOK_PREFIX + ":" + market;
    }

    public static String ticker(String market) {
        return TICKER_PREFIX + ":" + market;
    }

    public static String trade(String market) {
        return TRADE_PREFIX + ":" + market;
    }
    public static String market(String market) {
        return MARKET_PREFIX + ":" + market;
    }
    public static String limit(String market, String orderId) {
        return LIMIT_PREFIX + ":" + market + ":" + orderId;
    }
    public static String reservation(String market,String orderId) {
        return RESERVED_PREFIX + ":" + market + ":" + orderId;
    }
}