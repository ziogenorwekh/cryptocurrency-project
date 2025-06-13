package shop.shportfolio.user.application.security;

import shop.shportfolio.common.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.UserTokenItem;

public interface JwtToken {

    UserTokenItem decodeTemporalEmailAuthentication(Token token);
}
