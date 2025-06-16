package shop.shportfolio.user.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.update.*;
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

    void updatePassword(@Valid ResetAndNewPwdCommand resetAndNewPwdCommand);

    UploadUserImageResponse updateUserProfileImage(@Valid UploadUserImageCommand uploadUserImageCommand);

    void create2FASetting(@Valid TwoFactorEnableCommand twoFactorEnableCommand);

    void save2FA(@Valid TwoFactorEmailVerifyCodeCommand twoFactorEmailVerifyCodeCommand);

    void deleteUser(@Valid UserDeleteCommand userDeleteCommand);
}
