package shop.shportfolio.user.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdapter;
import shop.shportfolio.user.domain.entity.User;
@Slf4j
@Component
public class UserQueryHandler {


    private final UserRepositoryAdapter  userRepositoryAdapter;


    public UserQueryHandler(UserRepositoryAdapter userRepositoryAdapter) {
        this.userRepositoryAdapter = userRepositoryAdapter;
    }

    public User findOneUser(UserTrackQuery userTrackQuery) {
        return userRepositoryAdapter.findByUserId(userTrackQuery.getUserId()).orElseThrow(
                ()-> new UserNotfoundException(String.format("User with id %s not found", userTrackQuery.getUserId()))
        );
    }


    public Boolean existsUserByEmail(String email) {

        boolean isPresent = userRepositoryAdapter.findByEmail(email).isPresent();
        log.info(isPresent ? String.format("%s is present.", email) :
                String.format("User with email %s not found", email));
        return isPresent;
    }
}
