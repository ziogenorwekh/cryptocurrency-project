package shop.shportfolio.trading.domain.valueobject;

public enum OrderType {
    LIMIT,
    MARKET,
    RESERVATION;


    public boolean isReservationType() {
        return this.name().equals("RESERVATION");
    }

    public boolean isLimit() {
        return this.name().equals("LIMIT");
    }

    public boolean isMarket() {
        return this.name().equals("MARKET");
    }
}
