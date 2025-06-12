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
    private Role role;
    private ProfileImage profileImage;
    private List<TransactionHistory> transactionHistory;
    private SecuritySettings securitySettings;

//    create User in Domain Entity
    public User(Email email,PhoneNumber phoneNumber, Username username, Password password) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = new CreatedAt(now);
        this.role = new Role();
        this.profileImage = new ProfileImage(UUID.randomUUID(),"");
        transactionHistory = new ArrayList<>();
        this.securitySettings = new SecuritySettings(new SecuritySettingsId(UUID.randomUUID()));
    }

    public User(Email email, Username username, Password password,CreatedAt createdAt, Role role,
                List<TransactionHistory> transactionHistory, SecuritySettings securitySettings) {

    }

    public static User createUser(Email email,PhoneNumber phoneNumber, Username username, Password password) {
        isValidEmail(email);
        isValidUsername(username);
        return new User(email, phoneNumber, username, password);
    }

    public void updateProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }


    private static void isValidEmail(Email email) {
        if (!Email.isValidEmailStyle(email.getValue())) {
            throw new UserDomainException("Invalid email.");
        }
    }

    private static void isValidUsername(Username username) {
        if (!Username.isValidKoreanWord(username.getValue())) {
            throw new UserDomainException("Invalid username.");
        }
    }

    public void updatePassword(Password newPassword) {
        if (this.password.equals(newPassword)) {
            throw new  UserDomainException("Passwords is matched by before password.");
        }
        this.password = newPassword;
    }
}
