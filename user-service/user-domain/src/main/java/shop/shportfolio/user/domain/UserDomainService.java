package shop.shportfolio.user.domain;

import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.user.domain.event.UserCreatedEvent;
import shop.shportfolio.user.domain.event.UserDeletedEvent;
import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;


public interface UserDomainService {

    User createUser(UserId userId, Email email, PhoneNumber phoneNumber, Username username, Password password);

    void updatePassword(User user, Password password);

    void updateProfileImage(User user, ProfileImage profileImage);

    void grantRole(User user, RoleType roleType);

    void deleteRole(User user, RoleType roleType);

    void enable2FASecurity(User user);

    void userSelect2FASecurityMethod(User user, TwoFactorAuthMethod twoFactorAuthMethod);

    void disable2FASecurity(User user);

    UserDeletedEvent createUserDeletedEvent(UserId userId);

    UserCreatedEvent createUserCreatedEvent(UserId userId);
}
