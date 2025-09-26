package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.update.TwoFactorDisableCommand;
import shop.shportfolio.user.application.command.update.UploadUserImageCommand;
import shop.shportfolio.user.application.command.update.UserOldPasswordChangeCommand;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.event.UserDeletedEvent;

public interface UserUpdateDeleteUseCase {


    User uploadImage(UploadUserImageCommand uploadUserImageCommand);

    UserDeletedEvent deleteUser(UserDeleteCommand userDeleteCommand);

    void disableTwoFactorMethod(TwoFactorDisableCommand twoFactorDisableCommand);

    void updateOldPasswordToNewPassword(UserOldPasswordChangeCommand userOldPasswordChangeCommand);
}
