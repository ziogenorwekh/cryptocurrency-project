package shop.shportfolio.user.application;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.update.TwoFactorDisableCommand;
import shop.shportfolio.user.application.command.update.UploadUserImageCommand;
import shop.shportfolio.user.application.command.update.UserOldPasswordChangeCommand;
import shop.shportfolio.user.application.generator.FileGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.UserUpdateDeleteUseCase;
import shop.shportfolio.user.application.ports.output.s3.S3BucketAdapter;
import shop.shportfolio.user.domain.entity.User;

import java.io.File;

@Component
public class UserUpdateDeleteFacade implements UserUpdateDeleteUseCase {

    private final S3BucketAdapter s3BucketAdapter;
    private final UserCommandHandler userCommandHandler;
    private final FileGenerator fileGenerator;

    public UserUpdateDeleteFacade(S3BucketAdapter s3BucketAdapter, UserCommandHandler userCommandHandler,
                                  FileGenerator fileGenerator) {
        this.s3BucketAdapter = s3BucketAdapter;
        this.userCommandHandler = userCommandHandler;
        this.fileGenerator = fileGenerator;
    }


    @Override
    public User uploadImage(UploadUserImageCommand uploadUserImageCommand) {
        File file = fileGenerator.convertByteArrayToFile(uploadUserImageCommand.getUserId(), uploadUserImageCommand.getFileContent(),
                uploadUserImageCommand.getOriginalFileName());
        String s3ProfileImageUrl = s3BucketAdapter.uploadS3ProfileImage(file);
        return userCommandHandler.updateProfileImage(uploadUserImageCommand.getUserId(),
                uploadUserImageCommand.getOriginalFileName(), s3ProfileImageUrl);
    }

    @Override
    public void deleteUser(UserDeleteCommand userDeleteCommand) {
        userCommandHandler.deleteUserByUserId(userDeleteCommand.getUserId());
    }

    @Override
    public void disableTwoFactorMethod(TwoFactorDisableCommand twoFactorDisableCommand) {
        userCommandHandler.disableTwoFactor(twoFactorDisableCommand.getUserId());
    }

    @Override
    public void updateOldPasswordToNewPassword(UserOldPasswordChangeCommand userOldPasswordChangeCommand) {
        User user = userCommandHandler.findUserByUserId(userOldPasswordChangeCommand.getUserId());
        userCommandHandler.updatePasswordWithCurrent(userOldPasswordChangeCommand.getOldPassword(),
                userOldPasswordChangeCommand.getNewPassword(), user);
    }
}
