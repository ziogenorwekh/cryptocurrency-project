package shop.shportfolio.user.application.handler;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdapter;
import shop.shportfolio.user.domain.entity.User;

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
}
