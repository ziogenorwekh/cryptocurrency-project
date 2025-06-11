package shop.shportfolio.user.domain.valueobject;

public class Password {

    private final String value;

    public Password(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
