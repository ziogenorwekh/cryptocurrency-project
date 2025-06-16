package application.test;

import application.tmpbean.TestUserApplicationMockBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.ports.input.UserApplicationService;
import shop.shportfolio.user.application.command.update.UploadUserImageCommand;
import shop.shportfolio.user.application.command.update.UploadUserImageResponse;
import shop.shportfolio.user.application.generator.FileGenerator;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.application.ports.output.s3.S3BucketAdapter;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.ProfileImage;
import shop.shportfolio.user.domain.valueobject.Username;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TestUserApplicationMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceUpdateProfileImageTest {


    @Autowired
    private UserApplicationService userApplicationService;
    @Autowired
    private S3BucketAdapter s3BucketAdapter;

    @Autowired
    private FileGenerator fileGenerator;

    @Autowired
    private UserRepositoryAdaptor userRepositoryAdaptor;

    private final String username = "김철수";
    private final String phoneNumber = "01012345678";
    private final String email = "test@example.com";
    private final String password = "testpwd";
    private final UUID userId = UUID.randomUUID();
//    private final String code = "123456";
    private final String fileUrl = "/../../../test.jpg";
    User testUser = User.createUser(new UserId(userId), new Email(email),
            new PhoneNumber(phoneNumber), new Username(username), new Password(password));


    @Test
    @DisplayName("프로필 이미지 업데이트")
    public void updateUserProfileImageTest() throws IOException {
        // given
        byte[] imageBytes = "dummy-image-bytes".getBytes();
        String filename = "profile.png";

        UploadUserImageCommand uploadUserImageCommand = new UploadUserImageCommand(
                userId,
                filename,
                imageBytes
        );

        File tempFile = File.createTempFile(userId.toString(), "_profile");
        ProfileImage profileImage = ProfileImage.builder()
                .value(testUser.getProfileImage().getValue())
                .profileImageExtensionWithName(filename)
                .fileUrl(fileUrl)
                .build();

        User updatedUser = testUser;
        updatedUser.updateProfileImage(profileImage);

        Mockito.when(userRepositoryAdaptor.findByUserId(userId))
                .thenReturn(Optional.of(testUser));

        Mockito.when(fileGenerator.convertByteArrayToFile(
                Mockito.eq(userId),
                Mockito.eq(imageBytes),
                Mockito.eq(filename)
        )).thenReturn(tempFile);

        Mockito.when(s3BucketAdapter.uploadS3ProfileImage(tempFile))
                .thenReturn(fileUrl);

        Mockito.when(userRepositoryAdaptor.save(testUser))
                .thenReturn(updatedUser);

        // when
        UploadUserImageResponse uploadUserImageResponse =
                userApplicationService.updateUserProfileImage(uploadUserImageCommand);

        // then
        Mockito.verify(s3BucketAdapter, Mockito.times(1)).uploadS3ProfileImage(tempFile);
        Mockito.verify(fileGenerator, Mockito.times(1))
                .convertByteArrayToFile(userId, imageBytes, filename);

        Assertions.assertNotNull(uploadUserImageResponse);
        Assertions.assertEquals(fileUrl, uploadUserImageResponse.getFileUrl());
        Assertions.assertEquals(filename, uploadUserImageResponse.getFileName());

        tempFile.deleteOnExit();
    }

}
