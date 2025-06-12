package shop.shportfolio.user.domain.valueobject;

public class OtpToken {

    private final String otpToken;
    public OtpToken(String otpToken) {

        this.otpToken = otpToken;
    }

    public String getOtpToken() {
        return otpToken;
    }

}
