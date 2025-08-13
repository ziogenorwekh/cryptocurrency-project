package shop.shportfolio.user.application.usecase;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.command.track.UserTwoFactorTrackQuery;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.ports.input.UserTrackUseCase;
import shop.shportfolio.user.domain.entity.SecuritySettings;
import shop.shportfolio.user.domain.entity.User;

@Component
public class UserTrackUseCaseUseCaseImpl implements UserTrackUseCase {

    private final UserQueryHandler userQueryHandler;

    public UserTrackUseCaseUseCaseImpl(UserQueryHandler userQueryHandler) {
        this.userQueryHandler = userQueryHandler;
    }

    @Override
    public SecuritySettings trackUserTwoFactor(UserTwoFactorTrackQuery userTwoFactorTrackQuery) {

        return userQueryHandler.findUserSecuritySettingsByUserId(userTwoFactorTrackQuery.getUserId());
    }

    @Override
    public User trackUser(UserTrackQuery userTrackQuery) {
        return userQueryHandler.findOneUser(userTrackQuery.getUserId());
    }
}
