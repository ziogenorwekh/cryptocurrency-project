package shop.shportfolio.user.application;

import jakarta.validation.Valid;
import shop.shportfolio.user.application.command.reset.PwdUpdateTokenResponse;
import shop.shportfolio.user.application.command.reset.PwdUpdateTokenCommand;
import shop.shportfolio.user.application.command.reset.UserPwdResetCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;

public interface UserApplicationService {

    UserCreatedResponse createUser(@Valid UserCreateCommand userCreateCommand);

    TrackUserQueryResponse trackUserQuery(@Valid UserTrackQuery userTrackQuery);

    void sendTempEmailCodeForCreateUser(@Valid
            UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand);

    VerifiedTempEmailUserResponse verifyTempEmailCodeForCreateUser(@Valid
            UserTempEmailAuthVerifyCommand userTempEmailAuthVerifyCommand);

    void sendMailResetPwd(@Valid UserPwdResetCommand userPwdResetCommand);

    PwdUpdateTokenResponse validateResetTokenForPasswordUpdate(@Valid PwdUpdateTokenCommand pwdUpdateTokenCommand);
}
