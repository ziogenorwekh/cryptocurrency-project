package shop.shportfolio.user.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.exception.InvalidPasswordException;
import shop.shportfolio.user.application.exception.UserDuplicationException;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryPort;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderPort;
import shop.shportfolio.user.domain.UserDomainService;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.ProfileImage;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;
import shop.shportfolio.user.domain.valueobject.Username;

import java.util.UUID;

@Slf4j
@Component
public class UserCommandHandler {


    private final UserRepositoryPort userRepositoryPort;
    private final UserDomainService userDomainService;
    private final PasswordEncoderPort passwordEncoderPort;

    @Autowired
    public UserCommandHandler(UserRepositoryPort userRepositoryPort, UserDomainService userDomainService,
                              PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.userDomainService = userDomainService;
        this.passwordEncoderPort = passwordEncoderPort;
    }


    public User createUser(UUID newUserId, String stringEmail, String stringPhoneNumber,
                           String stringUsername, String encryptedPassword) {
        // 토큰이 들어오면 되는데.
        isDuplicatedEmail(stringEmail);
        isAlreadyExistsPhone(stringPhoneNumber);
        isAlreadyExistsUsername(stringUsername);
        UserId userId = new UserId(newUserId);
        Email email = new Email(stringEmail);
        PhoneNumber phoneNumber = new PhoneNumber(stringPhoneNumber);
        Username username = new Username(stringUsername);
        Password password = new Password(encryptedPassword);

        User user = userDomainService.createUser(userId, email, phoneNumber, username, password);
        return userRepositoryPort.save(user);
    }

    public void setNewPasswordAfterReset(String newPassword, User user) {
        userDomainService.updatePassword(user, new Password(newPassword));
        userRepositoryPort.save(user);
        log.info("User with id {} has been updated password", user.getId());
    }

    public void updatePasswordWithCurrent(String oldPassword, String newPassword, User user) {
        if (!passwordEncoderPort.matches(oldPassword, user.getPassword().getValue())) {
            throw new InvalidPasswordException("current password does not match current encrypted password");
        }
        boolean matches = passwordEncoderPort.matches(newPassword, user.getPassword().getValue());
        if (matches) {
            throw new InvalidPasswordException("password must not match old password");
        }
        String encoded = passwordEncoderPort.encode(newPassword);
        userDomainService.updatePassword(user, new Password(encoded));
        userRepositoryPort.save(user);
    }


    public User updateProfileImage(UUID userId, String originalFileName, String url) {
        User user = userRepositoryPort.findByUserId(userId).orElseThrow(() ->
                new UserNotfoundException(String.format("%s is not found", userId)));
        ProfileImage profileImage = ProfileImage.builder().value(user.getProfileImage().getValue())
                .profileImageExtensionWithName(originalFileName).fileUrl(url).build();
        userDomainService.updateProfileImage(user, profileImage);
        log.info("Profile image has been updated is userId: {}", user.getId());
        return userRepositoryPort.save(user);
    }

    public User findUserByUserId(UUID userId) {
        log.info("Finding user by user id {}", userId);
        return userRepositoryPort.findByUserId(userId).orElseThrow(() ->
                new UserNotfoundException(String.format("%s is not found", userId)));
    }


    public void save2FA(User user, TwoFactorAuthMethod twoFactorAuthMethod) {
        userDomainService.userSelect2FASecurityMethod(user, twoFactorAuthMethod);
        userDomainService.enable2FASecurity(user);
        log.info("User {} has been saved successfully 2FA Settings", user.getUsername());
        userRepositoryPort.save(user);
    }

    public UserId deleteUserByUserId(UUID userId) {
        User user = userRepositoryPort.findByUserId(userId)
                .orElseThrow(() ->
                        new UserNotfoundException(String.format("%s is not found", userId)));
        log.warn("Delete user by id {}", user.getId());
        userRepositoryPort.deleteUserById(user.getId().getValue());
        return user.getId();
    }

    public void disableTwoFactor(UUID userId) {
        User user = this.findUserByUserId(userId);
        log.info("UserId {} has been disabled", user.getId());
        userDomainService.disable2FASecurity(user);
        userRepositoryPort.save(user);
    }


    public void isDuplicatedEmail(String email) {
        userRepositoryPort.findByEmail(email)
                .ifPresent(user -> {
                    throw new UserDuplicationException(String.format("User with email %s already exists", email));
                });
    }

    public User findUserByEmail(String email) {
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotfoundException(String.format("User with email %s is not found", email)));
    }

    private void isAlreadyExistsPhone(String phone) {
        userRepositoryPort.findByPhoneNumber(phone)
                .ifPresent(user -> {
                    throw new UserDuplicationException(String.format("User with phone number %s already exists", phone));
                });
    }

    private void isAlreadyExistsUsername(String username) {
        userRepositoryPort.findByUsername(username)
                .ifPresent(user -> {
                    throw new UserDuplicationException(String.format("User with username %s already exists", username));
                });
    }
}
