package shop.shportfolio.common.domain.valueobject;

public class Token {
    private final String value;

    public Token(String value) {
        this.value = value;
    }

    public String getToken() {
        return value;
    }
}
