package shop.shportfolio.common.domain.valueobject;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ValueObject<T> {

    protected final T value;

    public ValueObject(T value) {
        if (value == null) {
            throw new IllegalArgumentException("ValueObject cannot be null.");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ValueObject<?> that = (ValueObject<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
