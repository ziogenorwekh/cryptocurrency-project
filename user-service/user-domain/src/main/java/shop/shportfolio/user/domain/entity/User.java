package shop.shportfolio.user.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.user.domain.exception.UserDomainException;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.Username;

import java.time.LocalDateTime;

@Getter
public class User extends AggregateRoot<UserId> {


    private Email email;
    private Username username;
    private Password password;
    private CreatedAt createdAt;
    private UpdatedAt updatedAt;
    private Role role;
    private TransactionHistory transactionHistory;
    private SecuritySettings securitySettings;

    public User(Email email, Username username, Password password) {
        this.email = email;
        this.username = username;
        this.password = password;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = new CreatedAt(now);
        this.updatedAt = new UpdatedAt(now);
        this.role = new Role();
        this.transactionHistory = new TransactionHistory();
        this.securitySettings = new SecuritySettings();
    }

    public static User createUser(Email email, Username username, Password password) {
        isValidEmail(email);
        isValidUsername(username);
        return new User(email, username, password);
    }


    private static void isValidEmail(Email email) {
        if (!Email.isValidEmailStyle(email.getValue())) {
            throw new UserDomainException("Invalid email.");
        }
    }

    private static void isValidUsername(Username username) {
        if (!Username.isValidKoreanWord(username.getUsername())) {
            throw new UserDomainException("Invalid username.");
        }
    }

}
