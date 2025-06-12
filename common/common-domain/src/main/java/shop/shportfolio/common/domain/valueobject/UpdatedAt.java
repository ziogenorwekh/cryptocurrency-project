package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;

public class UpdatedAt extends ValueObject<LocalDateTime> {

    public UpdatedAt(LocalDateTime value) {
        super(value);
    }
    public LocalDateTime getValue() {
        return value;
    }
}
