package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.ProfileImage;
import shop.shportfolio.user.domain.valueobject.Username;


public interface UserDomainService {

    User createUser(Email email, PhoneNumber phoneNumber, Username username, Password password);

    void updatePassword(User user, Password password);

    void updateProfileImage(User user, ProfileImage profileImage);

}
