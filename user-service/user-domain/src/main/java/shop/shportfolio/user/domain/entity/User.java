package shop.shportfolio.user.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.user.domain.exception.UserDomainException;
import shop.shportfolio.user.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends AggregateRoot<UserId> {

    private Email email;
    private Username username;
    private Password password;
    private PhoneNumber phoneNumber;
    private CreatedAt createdAt;
    private List<Role> roles;
    private ProfileImage profileImage;
    private SecuritySettings securitySettings;

    //    create User in Domain Entity
    public User(UserId userId, Email email, PhoneNumber phoneNumber, Username username, Password password) {
        setId(userId);
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = new CreatedAt(now);
        this.roles = new ArrayList<>();
        this.grantRole(RoleType.USER);
        this.securitySettings = new SecuritySettings(new SecuritySettingsId(UUID.randomUUID()));
        this.profileImage = new ProfileImage(UUID.randomUUID(), "", "");
    }

    @Builder
    public User(UUID userId, String email, String username, String password, String phoneNumber, LocalDateTime createdAt,
                List<Role> roles, ProfileImage profileImage, SecuritySettings securitySettings) {
        setId(new UserId(userId));
        this.email = new Email(email);
        this.username = new Username(username);
        this.password = new Password(password);
        this.phoneNumber = new  PhoneNumber(phoneNumber);
        this.createdAt = new  CreatedAt(createdAt);
        this.roles = roles;
        this.profileImage = profileImage;
        this.securitySettings = securitySettings;
    }

    public static User createUser(UserId userId, Email email, PhoneNumber phoneNumber, Username username, Password password) {
        isValidEmail(email);
        isValidUsername(username);
        return new User(userId, email, phoneNumber, username, password);
    }

    public void updateProfileImage(ProfileImage profileImage) {
        if (profileImage == null) {
            throw new UserDomainException("Request Profile image cannot be null");
        }
        this.profileImage = profileImage;
    }

    public void updatePassword(Password newPassword) {
        if (newPassword == null) {
            throw new UserDomainException("Request Password cannot be null");
        }
        this.password = newPassword;
    }

    public void userUse2FASecurity() {
        this.securitySettings.enable();
    }

    public void userSelect2FASecurityMethod(TwoFactorAuthMethod twoFactorAuthMethod) {
        if (twoFactorAuthMethod == null) {
            throw new UserDomainException("Request Two Factor Authentication cannot be null");
        }
        this.securitySettings.setTwoFactorAuthMethod(twoFactorAuthMethod);
    }

    public void grantRole(RoleType roleType) {
        if (roleType == null) {
            throw new UserDomainException("Request Role Type cannot be null");
        }
        boolean alreadyGranted = roles.stream()
                .anyMatch(r -> r.getRoleType().equals(roleType));
        if (alreadyGranted) {
            throw new UserDomainException(String.format("%s is already granted to this user", roleType));
        }
        UUID roleId = UUID.randomUUID();
        Role role = new Role(new RoleId(roleId));
        role.grantRole(roleType);
        roles.add(role);
    }

    public void deleteRole(RoleType roleType) {
        if (roleType == null) {
            throw new UserDomainException("Request Role Type cannot be null");
        }
        this.roles.removeIf(r -> r.getRoleType().equals(roleType));
    }

    public void disable2FA() {
        this.securitySettings.disable();
    }

    private static void isValidEmail(Email email) {
        if (email == null) {
            throw new UserDomainException("Request Email cannot be null");
        }
        if (!Email.isValidEmailStyle(email.getValue())) {
            throw new UserDomainException("Invalid email");
        }
    }

    private static void isValidUsername(Username username) {
        if (username == null) {
            throw new UserDomainException("Request Username cannot be null");
        }
        if (!Username.isValidKoreanWord(username.getValue())) {
            throw new UserDomainException("Invalid username");
        }
    }
}
