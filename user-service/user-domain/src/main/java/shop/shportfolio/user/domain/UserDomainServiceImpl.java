package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;


public class UserDomainServiceImpl implements UserDomainService {
    @Override
    public User createUser(Email email, PhoneNumber phoneNumber, Username username, Password password) {
        return User.createUser(email, phoneNumber, username, password);
    }

    @Override
    public void updatePassword(User user, Password password) {
        user.updatePassword(password);
    }

    @Override
    public void updateProfileImage(User user, ProfileImage profileImage) {
        user.updateProfileImage(profileImage);
    }

    @Override
    public void grantRole(User user, RoleType roleType) {
        user.grantRole(roleType);
    }

    @Override
    public void enable2FASecurity(User user) {
        user.userUse2FASecurity();
    }

    @Override
    public void userSelect2FASecurityMethod(User user, TwoFactorAuthMethod twoFactorAuthMethod) {
        user.userSelect2FASecurityMethod(twoFactorAuthMethod);
    }

    @Override
    public void disable2FASecurity(User user) {
        user.disable2FA();
    }

}
