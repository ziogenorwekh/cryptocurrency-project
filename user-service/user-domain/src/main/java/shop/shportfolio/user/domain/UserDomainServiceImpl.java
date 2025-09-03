package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.user.domain.event.UserCreatedEvent;
import shop.shportfolio.user.domain.event.UserDeletedEvent;
import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;


public class UserDomainServiceImpl implements UserDomainService {
    @Override
    public User createUser(UserId userId, Email email, PhoneNumber phoneNumber, Username username, Password password) {
        return User.createUser(userId, email, phoneNumber, username, password);
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
    public void deleteRole(User user, RoleType roleType) {
        user.deleteRole(roleType);
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

    @Override
    public UserDeletedEvent createUserDeletedEvent(UserId userId) {
        return new UserDeletedEvent(userId, MessageType.DELETE, ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public UserCreatedEvent createUserCreatedEvent(UserId userId) {
        return new UserCreatedEvent(userId, MessageType.CREATE, ZonedDateTime.now(ZoneOffset.UTC));
    }

}
