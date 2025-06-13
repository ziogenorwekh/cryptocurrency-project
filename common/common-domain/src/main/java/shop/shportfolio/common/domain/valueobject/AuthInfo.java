package shop.shportfolio.common.domain.valueobject;

public class AuthInfo {

    private final AuthCodeType authCodeType;
    private final String value;

    public AuthInfo(AuthCodeType authCodeType, String value) {
        this.authCodeType = authCodeType;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public AuthCodeType getAuthCodeType() {
        return authCodeType;
    }
}
