package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.BaseId;

import java.util.UUID;

public class TradeId extends BaseId<UUID> {


    protected TradeId(UUID value) {
        super(value);
    }

    @Override
    public UUID getValue() {
        return super.getValue();
    }
}
