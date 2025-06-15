package shop.shportfolio.user.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.domain.entity.User;
@Slf4j
@Component
public class UserQueryHandler {


    private final UserRepositoryAdaptor userRepositoryAdaptor;


    public UserQueryHandler(UserRepositoryAdaptor userRepositoryAdaptor) {
        this.userRepositoryAdaptor = userRepositoryAdaptor;
    }

    public User findOneUser(UserTrackQuery userTrackQuery) {
        return userRepositoryAdaptor.findByUserId(userTrackQuery.getUserId()).orElseThrow(
                ()-> new UserNotfoundException(String.format("User with id %s not found", userTrackQuery.getUserId()))
        );
    }
    public User findOneUserByEmail(String email) {
        return userRepositoryAdaptor.findByEmail(email).orElseThrow(
                ()-> new UserNotfoundException(String.format("User with email %s not found", email))
        );
    }

    public Boolean existsUserByEmail(String email) {
        boolean isPresent = userRepositoryAdaptor.findByEmail(email).isPresent();
        log.info(isPresent ? String.format("%s is present.", email) :
                String.format("User with email %s not found", email));
        return isPresent;
    }
}
