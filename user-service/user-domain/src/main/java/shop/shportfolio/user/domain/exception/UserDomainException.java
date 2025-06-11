package shop.shportfolio.user.domain.exception;

import shop.shportfolio.common.domain.exception.DomainException;

public class UserDomainException extends DomainException {

    public UserDomainException(String message) {
        super(message);
    }
}
