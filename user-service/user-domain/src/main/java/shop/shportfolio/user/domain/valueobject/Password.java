package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.Objects;

public class Password  extends ValueObject<String> {


    public Password(String value) {
        super(value);
    }
    public String getValue() {
        return value;
    }

}
