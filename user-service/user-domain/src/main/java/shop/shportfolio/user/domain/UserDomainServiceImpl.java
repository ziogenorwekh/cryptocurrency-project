package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.Username;


public class UserDomainServiceImpl implements UserDomainService {
    @Override
    public User createUser(Email email, Username username, Password password) {
        return User.createUser(email, username, password);
    }

}
