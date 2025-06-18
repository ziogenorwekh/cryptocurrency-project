package shop.shportfolio.trading.domain.valueobject;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.BaseId;

import java.util.UUID;

@Getter
public class PriceLevelId extends BaseId<UUID> {

    protected PriceLevelId(UUID value) {
        super(value);
    }
}
