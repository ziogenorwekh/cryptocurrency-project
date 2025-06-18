package shop.shportfolio.user.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.domain.entity.SecuritySettings;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

@Slf4j
@Component
public class UserQueryHandler {


    private final UserRepositoryAdaptor userRepositoryAdaptor;


    public UserQueryHandler(UserRepositoryAdaptor userRepositoryAdaptor) {
        this.userRepositoryAdaptor = userRepositoryAdaptor;
    }

    public User findOneUser(UUID userId) {
        User user = userRepositoryAdaptor.findByUserId(userId).orElseThrow(
                () -> new UserNotfoundException(String.format("User with id %s not found", userId))
        );
        log.info("User with id {} found", userId);
        return user;
    }
    public User findOneUserByEmail(String email) {
        User user = userRepositoryAdaptor.findByEmail(email).orElseThrow(
                ()-> new UserNotfoundException(String.format("User with email %s not found", email))
        );
        log.info("User with email {} found", email);
        return user;
    }

    public SecuritySettings findUserSecuritySettingsByUserId(UUID userId) {
        User user = userRepositoryAdaptor.findByUserId(userId).orElseThrow(
                () -> new UserNotfoundException(String.format("User with id %s not found", userId)));
        log.info("User with id {} found", userId);
        return user.getSecuritySettings();
    }
}
