package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.regex.Pattern;

public class Username extends ValueObject<String> {

    private static final Pattern NICKNAME_ALLOWED_CHAR_REGEX =
            Pattern.compile("^[가-힣]*$");

    public Username(String value) {
        super(value);
    }
    public static boolean isValidKoreanWord(String nicknameString) {
        // null 또는 빈 문자열 체크 (기본 유효성)
        if (nicknameString == null || nicknameString.trim().isEmpty()) {
            return false;
        }
        // 특수 문자 포함 여부 검증 (정규식에 정의된 허용 문자 외의 문자 검사)
        return NICKNAME_ALLOWED_CHAR_REGEX.matcher(nicknameString).matches();
    }
}
