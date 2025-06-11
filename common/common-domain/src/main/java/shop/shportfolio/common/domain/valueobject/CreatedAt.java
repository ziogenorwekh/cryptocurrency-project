package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;

public class CreatedAt {
    private final LocalDateTime value;

    public CreatedAt(LocalDateTime value) {
        this.value = value;
    }
    public LocalDateTime getValue() {
        return value;
    }

}
