package shop.shportfolio.common.domain.valueobject;

public enum TransactionType {
    DEPOSIT, WITHDRAWAL, TRADE_BUY, TRADE_SELL;

    public static TransactionType fromString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        switch (input.toUpperCase()) {
            case "BUY":
                return TRADE_BUY;
            case "SELL":
                return TRADE_SELL;
            case "DEPOSIT":
                return DEPOSIT;
            case "WITHDRAWAL":
                return WITHDRAWAL;
            default:
                throw new IllegalArgumentException("Unknown TransactionType: " + input);
        }
    }


}
