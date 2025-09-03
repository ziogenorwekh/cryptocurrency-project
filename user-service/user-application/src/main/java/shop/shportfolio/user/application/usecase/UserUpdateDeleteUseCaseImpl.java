package shop.shportfolio.user.application.usecase;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.update.TwoFactorDisableCommand;
import shop.shportfolio.user.application.command.update.UploadUserImageCommand;
import shop.shportfolio.user.application.command.update.UserOldPasswordChangeCommand;
import shop.shportfolio.user.application.generator.FileGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.UserUpdateDeleteUseCase;
import shop.shportfolio.user.application.ports.output.kafka.UserDeletedPublisher;
import shop.shportfolio.user.application.ports.output.s3.S3BucketPort;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.event.UserDeletedEvent;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class UserUpdateDeleteUseCaseImpl implements UserUpdateDeleteUseCase {

    private final S3BucketPort s3BucketPort;
    private final UserCommandHandler userCommandHandler;
    private final FileGenerator fileGenerator;
    private final UserDeletedPublisher userDeletedPublisher;

    public UserUpdateDeleteUseCaseImpl(S3BucketPort s3BucketPort, UserCommandHandler userCommandHandler,
                                       FileGenerator fileGenerator,
                                       UserDeletedPublisher userDeletedPublisher) {
        this.s3BucketPort = s3BucketPort;
        this.userCommandHandler = userCommandHandler;
        this.fileGenerator = fileGenerator;
        this.userDeletedPublisher = userDeletedPublisher;
    }


    @Override
    public User uploadImage(UploadUserImageCommand uploadUserImageCommand) {
        User user = userCommandHandler.findUserByUserId(uploadUserImageCommand.getUserId());
        s3BucketPort.deleteS3ProfileImage(user.getProfileImage().getProfileImageExtensionWithName());
        File file = fileGenerator.convertByteArrayToFile(uploadUserImageCommand.getUserId(),
                uploadUserImageCommand.getFileContent(),
                uploadUserImageCommand.getOriginalFileName());
        String s3ProfileImageUrl = s3BucketPort.uploadS3ProfileImage(file);
        return userCommandHandler.updateProfileImage(uploadUserImageCommand.getUserId(),
                uploadUserImageCommand.getOriginalFileName(), s3ProfileImageUrl);
    }

    @Override
    public void deleteUser(UserDeleteCommand userDeleteCommand) {
        UserDeletedEvent userDeletedEvent = userCommandHandler.deleteUserByUserId(userDeleteCommand.getUserId());
        userDeletedPublisher.publish(userDeletedEvent);
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
