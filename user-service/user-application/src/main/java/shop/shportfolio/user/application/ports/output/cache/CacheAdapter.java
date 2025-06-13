package shop.shportfolio.user.application.ports.output.cache;

import java.util.UUID;

public interface CacheAdapter {



    Boolean isAuthenticatedUserId(UUID userId);

}
