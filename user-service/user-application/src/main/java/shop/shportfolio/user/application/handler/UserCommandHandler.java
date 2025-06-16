package shop.shportfolio.user.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.update.UploadUserImageCommand;
import shop.shportfolio.user.application.exception.UserDuplicationException;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
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


    private final UserRepositoryAdaptor userRepositoryAdaptor;
    private final UserDomainService userDomainService;


    @Autowired
    public UserCommandHandler(UserRepositoryAdaptor userRepositoryAdaptor, UserDomainService userDomainService) {
        this.userRepositoryAdaptor = userRepositoryAdaptor;
        this.userDomainService = userDomainService;
    }


    public User createUser(UserCreateCommand userCreateCommand, String encryptedPassword) {
        // 토큰이 들어오면 되는데.
        isAlreadyExistsEmail(userCreateCommand.getEmail());
        isAlreadyExistsPhone(userCreateCommand.getPhoneNumber());
        isAlreadyExistsUsername(userCreateCommand.getUsername());
        UserId userId = new UserId(userCreateCommand.getUserId());
        Email email = new Email(userCreateCommand.getEmail());
        PhoneNumber phoneNumber = new PhoneNumber(userCreateCommand.getPhoneNumber());
        Username username = new Username(userCreateCommand.getUsername());
        Password password = new Password(encryptedPassword);

        User user = userDomainService.createUser(userId, email, phoneNumber, username, password);
        return userRepositoryAdaptor.save(user);
    }

    public void updatePassword(String newPassword, UUID userId) {
        User user = userRepositoryAdaptor.findByUserId(userId).orElseThrow(() ->
                new UserNotfoundException(String.format("%s is not found", userId)));
        userDomainService.updatePassword(user, new Password(newPassword));
        userRepositoryAdaptor.save(user);
    }


    public User updateProfileImage(UploadUserImageCommand uploadUserImageCommand, String url) {
        User user = userRepositoryAdaptor.findByUserId(uploadUserImageCommand.getUserId()).orElseThrow(() ->
                new UserNotfoundException(String.format("%s is not found", uploadUserImageCommand.getUserId())));
        ProfileImage profileImage = ProfileImage.builder().value(user.getProfileImage().getValue())
                .profileImageExtensionWithName(uploadUserImageCommand.getOriginalFileName()).fileUrl(url).build();
        userDomainService.updateProfileImage(user, profileImage);
        return userRepositoryAdaptor.save(user);
    }

    public User findUserByUserId(UUID userId) {
        log.info("Finding user by user id {}", userId);
        return userRepositoryAdaptor.findByUserId(userId).orElseThrow(() ->
                new UserNotfoundException(String.format("%s is not found", userId)));
    }


    public void save2FA(User user,TwoFactorAuthMethod twoFactorAuthMethod) {
        userDomainService.userSelect2FASecurityMethod(user, twoFactorAuthMethod);
        userDomainService.enable2FASecurity(user);
        log.info("User {} has been saved successfully 2FA Settings", user.getUsername());
        userRepositoryAdaptor.save(user);
    }

    public void deleteUserByUserId(UserDeleteCommand userDeleteCommand) {
        User user = userRepositoryAdaptor.findByUserId(userDeleteCommand.getUserId())
                .orElseThrow(() ->
                        new UserNotfoundException(String.format("%s is not found", userDeleteCommand.getUserId())));
        log.warn("Delete user by id {}", user.getId());
        userRepositoryAdaptor.deleteUserById(user.getId().getValue());
    }


    public void isAlreadyExistsEmail(String email) {
        userRepositoryAdaptor.findByEmail(email)
                .ifPresent(user -> {
                    throw new UserDuplicationException(String.format("User with email %s already exists", email));
                });
    }

    private void isAlreadyExistsPhone(String phone) {
        userRepositoryAdaptor.findByPhoneNumber(phone)
                .ifPresent(user -> {
                    throw new UserDuplicationException(String.format("User with phone number %s already exists", phone));
                });
    }

    private void isAlreadyExistsUsername(String username) {
        userRepositoryAdaptor.findByUsername(username)
                .ifPresent(user -> {
                    throw new UserDuplicationException(String.format("User with username %s already exists", username));
                });
    }
}
