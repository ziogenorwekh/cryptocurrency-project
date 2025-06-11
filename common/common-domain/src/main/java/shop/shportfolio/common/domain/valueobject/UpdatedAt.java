package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;

public class UpdatedAt {
    private final LocalDateTime value;

    public UpdatedAt(LocalDateTime value) {
        this.value = value;
    }
    public LocalDateTime getValue() {
        return value;
    }
}
