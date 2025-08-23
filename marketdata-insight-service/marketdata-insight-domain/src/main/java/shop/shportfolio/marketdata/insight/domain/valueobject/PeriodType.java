package shop.shportfolio.marketdata.insight.domain.valueobject;

public enum PeriodType {
    THIRTY_MINUTES,
    ONE_HOUR,
    ONE_DAY,
    ONE_WEEK,
    ONE_MONTH;

    public static PeriodType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        try {
            return PeriodType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown PeriodType: " + value, ex);
        }
    }
}
