package shop.shportfolio.user.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.exception.UserDuplicationException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdapter;
import shop.shportfolio.user.domain.UserDomainService;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.Username;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserCommandHandler {


    private final UserRepositoryAdapter userRepositoryAdapter;
    private final UserDomainService userDomainService;

    @Autowired
    public UserCommandHandler(UserRepositoryAdapter userRepositoryAdapter, UserDomainService userDomainService) {
        this.userRepositoryAdapter = userRepositoryAdapter;
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

        User user = userDomainService.createUser(userId,email, phoneNumber, username, password);
        return userRepositoryAdapter.save(user);
    }




    private void isAlreadyExistsEmail(String email) {
        userRepositoryAdapter.findByEmail(email)
                .ifPresent(user -> {
                    throw new UserDuplicationException(String.format("User with email %s already exists", email));
                });
    }

    private void isAlreadyExistsPhone(String phone) {
        userRepositoryAdapter.findByPhoneNumber(phone)
                .ifPresent(user -> {
                    throw new UserDuplicationException(String.format("User with phone number %s already exists", phone));
                });
    }
    private void isAlreadyExistsUsername(String username) {
        userRepositoryAdapter.findByUsername(username)
                .ifPresent(user -> {
                    throw new UserDuplicationException(String.format("User with username %s already exists", username));
                });
    }
}
