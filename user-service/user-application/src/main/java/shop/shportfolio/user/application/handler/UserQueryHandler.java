package shop.shportfolio.user.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryPort;
import shop.shportfolio.user.domain.entity.SecuritySettings;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

@Slf4j
@Component
public class UserQueryHandler {


    private final UserRepositoryPort userRepositoryPort;


    public UserQueryHandler(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public User findOneUser(UUID userId) {
        User user = userRepositoryPort.findByUserId(userId).orElseThrow(
                () -> new UserNotfoundException(String.format("User with id %s not found", userId))
        );
        log.info("User with id {} found", userId);
        return user;
    }
    public User findOneUserByEmail(String email) {
        User user = userRepositoryPort.findByEmail(email).orElseThrow(
                ()-> new UserNotfoundException(String.format("User with email %s not found", email))
        );
        log.info("User with email {} found", email);
        return user;
    }

    public SecuritySettings findUserSecuritySettingsByUserId(UUID userId) {
        User user = userRepositoryPort.findByUserId(userId).orElseThrow(
                () -> new UserNotfoundException(String.format("User with id %s not found", userId)));
        log.info("User with id {} found", userId);
        return user.getSecuritySettings();
    }
}
