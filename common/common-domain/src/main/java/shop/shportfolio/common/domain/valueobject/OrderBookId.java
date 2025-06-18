package shop.shportfolio.common.domain.valueobject;

import java.util.UUID;

public class OrderBookId extends BaseId<UUID> {

    protected OrderBookId(UUID value) {
        super(value);
    }
}
