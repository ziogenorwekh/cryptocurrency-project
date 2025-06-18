package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;
import java.util.Objects;


// 	예약 주문 실행 시간 (선택적: 시간 기반 예약일 경우 사용)
public class ScheduledTime extends ValueObject<LocalDateTime> {

    public ScheduledTime(LocalDateTime value) {
        super(value);
        if (value == null) {
            throw new IllegalArgumentException("ScheduledTime cannot be null");
        }
    }

    public boolean isDue() {
        return !LocalDateTime.now().isBefore(getValue());
    }
}
