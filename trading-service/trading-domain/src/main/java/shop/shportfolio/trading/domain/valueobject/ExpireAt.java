package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

// 예약 주문 만료 시점 (조건 미달시 만료 처리 등)
public class ExpireAt extends ValueObject<LocalDateTime> {

    public ExpireAt(LocalDateTime value) {
        super(value);
    }

    public boolean isBefore(LocalDateTime value) {
        return this.value.isBefore(value);
    }

    public boolean isExpired() {
        return LocalDateTime.now(ZoneOffset.UTC).isAfter(getValue());
    }
}