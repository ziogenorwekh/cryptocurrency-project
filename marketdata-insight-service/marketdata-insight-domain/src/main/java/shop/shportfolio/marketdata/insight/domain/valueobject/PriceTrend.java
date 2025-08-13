package shop.shportfolio.marketdata.insight.domain.valueobject;

public enum PriceTrend {
    UP("상승"),
    DOWN("하락"),
    SIDEWAYS("횡보"),
    NEUTRAL("초기값");

    private final String description; // 필드 선언

    PriceTrend(String description) { // 생성자
        this.description = description;
    }

    public String getDescription() { // getter
        return description;
    }
}
