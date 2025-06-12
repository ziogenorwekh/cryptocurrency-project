package shop.shportfolio.user.application;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;

@Validated
public interface UserApplicationService {

    UserCreatedResponse createUser(@Valid UserCreateCommand userCreateCommand);

    TrackUserQueryResponse trackUserQuery(@Valid UserTrackQuery userTrackQuery);
}
