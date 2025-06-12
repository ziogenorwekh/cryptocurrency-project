package shop.shportfolio.user.domain.entity;

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
    public User(Email email, PhoneNumber phoneNumber, Username username, Password password) {
        setId(new UserId(UUID.randomUUID()));
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = new CreatedAt(now);
        this.roles = new ArrayList<>();
        this.grantRole(RoleType.USER);
        this.profileImage = new ProfileImage(UUID.randomUUID(), "");
        this.securitySettings = new SecuritySettings(new SecuritySettingsId(UUID.randomUUID()));
    }

    public static User createUser(Email email, PhoneNumber phoneNumber, Username username, Password password) {
        isValidEmail(email);
        isValidUsername(username);
        return new User(email, phoneNumber, username, password);
    }

    public void updateProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public void updatePassword(Password newPassword) {
        this.password = newPassword;
    }

    public void userUse2FASecurity() {
        this.securitySettings.enable();
    }

    public void userSelect2FASecurityMethod(TwoFactorAuthMethod twoFactorAuthMethod) {
        this.securitySettings.setTwoFactorAuthMethod(twoFactorAuthMethod);
    }

    public void grantRole(RoleType roleType) {
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

    public void disable2FA() {
        this.securitySettings.disable();
    }

    private static void isValidEmail(Email email) {
        if (!Email.isValidEmailStyle(email.getValue())) {
            throw new UserDomainException("Invalid email");
        }
    }

    private static void isValidUsername(Username username) {
        if (!Username.isValidKoreanWord(username.getValue())) {
            throw new UserDomainException("Invalid username");
        }
    }
}
