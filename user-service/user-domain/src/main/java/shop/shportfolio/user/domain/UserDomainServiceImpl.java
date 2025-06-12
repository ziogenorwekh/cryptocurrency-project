package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.ProfileImage;
import shop.shportfolio.user.domain.valueobject.Username;


public class UserDomainServiceImpl implements UserDomainService {
    @Override
    public User createUser(Email email, PhoneNumber phoneNumber, Username username, Password password) {
        return User.createUser(email,phoneNumber, username, password);
    }

    @Override
    public void updatePassword(User user, Password password) {
        user.updatePassword(password);
    }

    @Override
    public void updateProfileImage(User user, ProfileImage profileImage) {
        user.updateProfileImage(profileImage);
    }

}
