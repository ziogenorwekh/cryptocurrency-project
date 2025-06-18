package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.regex.Pattern;

public class Email extends ValueObject<String> {

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    public Email(String value) {
        super(value);
    }

    public static boolean isValidEmailStyle(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return EMAIL_REGEX.matcher(value).matches();
    }
}
