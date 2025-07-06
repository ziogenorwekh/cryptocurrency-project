package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.Description;

public enum ChangeType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금"),
    TRADE_BUY("매수 체결"),
    TRADE_SELL("매도 체결"),
    FEE("수수료"),
    ADJUSTMENT("조정");

    private final String defaultDescription;

    ChangeType(String desc) {
        this.defaultDescription = desc;
    }

    public Description getDefaultDescription() {
        return new Description(defaultDescription);
    }
}