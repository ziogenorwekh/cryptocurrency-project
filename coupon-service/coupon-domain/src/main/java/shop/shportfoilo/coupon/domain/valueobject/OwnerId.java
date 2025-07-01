package shop.shportfoilo.coupon.domain.valueobject;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

@Getter
public class OwnerId extends ValueObject<UUID> {
    public OwnerId(UUID value) {
        super(value);
    }
}
