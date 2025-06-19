package shop.shportfolio.trading.domain.valueobject;


import java.util.Objects;

public class OrderSide {
    public static final OrderSide BUY = new OrderSide("BUY");
    public static final OrderSide SELL = new OrderSide("SELL");

    private final String value;

    private OrderSide(String value) {
        if (!"BUY".equals(value) && !"SELL".equals(value)) {
            throw new IllegalArgumentException("OrderSide must be 'BUY' or 'SELL'");
        }
        this.value = value;
    }

    public static OrderSide of(String value) {
        if (value == null) throw new IllegalArgumentException("OrderSide cannot be null");
        return new OrderSide(value.toUpperCase());
    }

    public boolean isBuy() {
        return "BUY".equals(this.value);
    }

    public boolean isSell() {
        return "SELL".equals(this.value);
    }

    public boolean isOpposite(OrderSide other) {
        if (other == null) return false;
        return !this.value.equals(other.value);
    }

    public String getValue() {
        return value;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderSide)) return false;
        OrderSide that = (OrderSide) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
