package shop.shportfolio.trading.application.support;

import java.util.UUID;

public class UUIDSupport {

    private UUIDSupport() { }

    public static long uuidToLong(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID must not be null");
        }
        return uuid.getMostSignificantBits();
    }
}
