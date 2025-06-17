package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.update.TwoFactorDisableCommand;
import shop.shportfolio.user.application.command.update.UploadUserImageCommand;
import shop.shportfolio.user.application.command.update.UserOldPasswordChangeCommand;
import shop.shportfolio.user.domain.entity.User;

public interface UserUpdateDeleteUseCase {


    User uploadImage(UploadUserImageCommand uploadUserImageCommand);

    void deleteUser(UserDeleteCommand userDeleteCommand);

    void disableTwoFactorMethod(TwoFactorDisableCommand twoFactorDisableCommand);

    void updateOldPasswordToNewPassword(UserOldPasswordChangeCommand userOldPasswordChangeCommand);
}
