package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.user.domain.valueobject.Token;
import shop.shportfolio.user.application.command.update.UserUpdateNewPwdCommand;
import shop.shportfolio.user.application.command.update.UserPwdResetCommand;

public interface PasswordUpdateUseCase {

    void requestPasswordResetByEmail(UserPwdResetCommand userPwdResetCommand);
    Token verifyResetTokenAndIssueUpdateToken(String token);
    void updatePasswordWithVerifiedToken(UserUpdateNewPwdCommand userUpdateNewPwdCommand);
}
