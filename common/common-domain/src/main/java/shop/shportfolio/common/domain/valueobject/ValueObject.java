package shop.shportfolio.common.domain.valueobject;

import java.util.Objects;

public class ValueObject<T> {

    protected final T value;

    public ValueObject(T value) {
        this.value = value;
    }


    public T getValue() {
        return value;
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
