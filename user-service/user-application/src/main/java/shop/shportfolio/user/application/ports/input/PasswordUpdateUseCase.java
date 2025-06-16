package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.common.domain.valueobject.Token;
import shop.shportfolio.user.application.command.update.PwdUpdateTokenCommand;
import shop.shportfolio.user.application.command.update.ResetAndNewPwdCommand;
import shop.shportfolio.user.application.command.update.UserPwdResetCommand;

public interface PasswordUpdateUseCase {

    void sendMailResetPwd(UserPwdResetCommand userPwdResetCommand);
    Token validateResetTokenForPasswordUpdate(PwdUpdateTokenCommand pwdUpdateTokenCommand);
    void getTokenByUserIdForUpdatePassword(ResetAndNewPwdCommand resetAndNewPwdCommand);
}
