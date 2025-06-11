package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.Username;

public interface UserDomainService {

    User createUser(Email email, Username username, Password password);
    User getUserByUsername(Username username);
    User getUserByEmail(Email email);

}
