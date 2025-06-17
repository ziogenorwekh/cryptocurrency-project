package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.user.application.command.track.TrackUserTwoFactorResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.command.track.UserTwoFactorTrackQuery;
import shop.shportfolio.user.domain.entity.SecuritySettings;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

public interface UserTrackUseCase {

    SecuritySettings trackUserTwoFactor(UserTwoFactorTrackQuery userTwoFactorTrackQuery);



    User trackUser(UserTrackQuery userTrackQuery);

}
