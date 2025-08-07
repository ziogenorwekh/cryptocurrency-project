package shop.shportfolio.common.domain.valueobject;

public enum MessageType {
    CREATE, DELETE, FAIL, REJECT, UPDATE, NO_DEF;

    public static MessageType fromName(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return NO_DEF;
        }
    }
}
