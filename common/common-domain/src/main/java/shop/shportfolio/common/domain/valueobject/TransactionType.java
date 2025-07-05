package shop.shportfolio.common.domain.valueobject;

public enum TransactionType {
    DEPOSIT, WITHDRAWAL, TRADE_BUY, TRADE_SELL;

    public static TransactionType fromString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        return switch (input.toUpperCase()) {
            case "BUY" -> TRADE_BUY;
            case "SELL" -> TRADE_SELL;
            case "DEPOSIT" -> DEPOSIT;
            case "WITHDRAWAL" -> WITHDRAWAL;
            default -> throw new IllegalArgumentException("Unknown TransactionType: " + input);
        };
    }


}
