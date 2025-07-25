package shop.shportfolio.common.domain.view;

import shop.shportfolio.common.domain.valueobject.UpdatedAt;

import java.util.Objects;

public abstract class BaseView {

    protected UpdatedAt updatedAt;

    protected BaseView(UpdatedAt updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UpdatedAt getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(UpdatedAt updatedAt) {
        this.updatedAt = updatedAt;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseView baseView = (BaseView) o;
        return Objects.equals(updatedAt, baseView.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(updatedAt);
    }
}
