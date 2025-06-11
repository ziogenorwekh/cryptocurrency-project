package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.BaseId;

import java.util.UUID;

public class TransactionHistoryId extends BaseId<UUID> {
    public TransactionHistoryId(UUID value) {
        super(value);
    }
}
