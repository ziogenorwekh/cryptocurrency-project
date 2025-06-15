package shop.shportfolio.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.common.domain.valueobject.Token;
import shop.shportfolio.user.application.command.update.*;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.NotImplementedException;
import shop.shportfolio.user.application.exception.UserDuplicationException;
import shop.shportfolio.user.application.ports.input.PasswordResetUseCase;
import shop.shportfolio.user.application.ports.input.UserRegistrationUseCase;
import shop.shportfolio.user.application.generator.FileGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.input.UserApplicationService;
import shop.shportfolio.user.application.ports.input.UserTwoFactorAuthenticationUseCase;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.s3.S3BucketAdapter;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.io.File;
import java.util.UUID;

@Service
@Validated
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserCommandHandler userCommandHandler;
    private final UserDataMapper userDataMapper;
    private final UserQueryHandler userQueryHandler;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderAdapter mailSenderAdapter;
    private final S3BucketAdapter s3BucketAdapter;
    private final FileGenerator fileGenerator;
    private final UserRegistrationUseCase userRegistrationUseCase;
    private final PasswordResetUseCase passwordResetUseCase;
    private final UserTwoFactorAuthenticationUseCase userTwoFactorAuthenticationUseCase;
    @Autowired
    public UserApplicationServiceImpl(UserCommandHandler userCommandHandler,
                                      UserDataMapper userDataMapper, UserQueryHandler userQueryHandler,
                                      PasswordEncoder passwordEncoder,
                                      MailSenderAdapter mailSenderAdapter, S3BucketAdapter s3BucketAdapter, FileGenerator fileGenerator,
                                      UserRegistrationUseCase userRegistrationUseCase,
                                      PasswordResetUseCase passwordResetUseCase, UserTwoFactorAuthenticationUseCase userTwoFactorAuthenticationUseCase) {
        this.userCommandHandler = userCommandHandler;
        this.userDataMapper = userDataMapper;
        this.userQueryHandler = userQueryHandler;
        this.passwordEncoder = passwordEncoder;
        this.s3BucketAdapter = s3BucketAdapter;
        this.fileGenerator = fileGenerator;
        this.userRegistrationUseCase = userRegistrationUseCase;
        this.mailSenderAdapter = mailSenderAdapter;
        this.passwordResetUseCase = passwordResetUseCase;
        this.userTwoFactorAuthenticationUseCase = userTwoFactorAuthenticationUseCase;
    }


    /***
     * pwdencoder, commandhandler
     * @param userCreateCommand
     * @return
     */
    @Override
    public UserCreatedResponse createUser(UserCreateCommand userCreateCommand) {
        userRegistrationUseCase.isAuthenticatedTempUser(userCreateCommand.getUserId(), userCreateCommand.getEmail());
//       비밀번호를 암호화하고, 회원가입 핸들러로 이동하여 엔티티 객체로 리턴
        String encryptedPassword = passwordEncoder.encode(userCreateCommand.getPassword());
        User user = userCommandHandler.createUser(userCreateCommand, encryptedPassword);
        userRegistrationUseCase.deleteTempEmailCode(user.getEmail().getValue());
//        도메인 객체를 매퍼로 최종 response 값 리턴
        return userDataMapper.userEntityToUserCreatedResponse(user);
    }

    /***
     * queryhandler, mailsender
     * 회원가입을 위한 임시 이메일 인증 코드 발송
     * @param userTempEmailAuthRequestCommand 이메일
     * @return 인증할 코드
     */
    @Override
    public void sendTempEmailCodeForCreateUser(
            UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand) {
        if (userQueryHandler.existsUserByEmail(userTempEmailAuthRequestCommand.getEmail())) {
            throw new UserDuplicationException(String.format("User with email %s already exists",
                    userTempEmailAuthRequestCommand.getEmail()));
        }
        String code = userRegistrationUseCase.sendTempEmailCodeForCreateUser(userTempEmailAuthRequestCommand.getEmail());
        mailSenderAdapter.sendMailWithEmailAndCode(userTempEmailAuthRequestCommand.getEmail(), code);
    }


    /***
     *
     * 이메일에서 받은 인증코드를 통해서 캐시에 등록 시간 저장
     * @param userTempEmailAuthVerifyCommand 이메일과 인증 코드
     * @return 유저 아이디와 유저 이메일
     */
    @Override
    public VerifiedTempEmailUserResponse verifyTempEmailCodeForCreateUser(
            UserTempEmailAuthVerifyCommand userTempEmailAuthVerifyCommand) {
        UUID userId = userRegistrationUseCase.verifyTempEmailCodeAndCreateUserId(userTempEmailAuthVerifyCommand.getEmail(),
                userTempEmailAuthVerifyCommand.getCode());
        return userDataMapper.valueToVerifiedTempEmailUserResponse(userId, userTempEmailAuthVerifyCommand.getEmail());
    }

    /***
     * mailsender
     * 비밀번호를 잊어버린 유저는 가입된 계정의 비밀번호를 초기화
     * @param userPwdResetCommand 등록했던 이메일
     */
    @Override
    public void sendMailResetPwd(UserPwdResetCommand userPwdResetCommand) {
        Token token = passwordResetUseCase.sendMailResetPwd(userPwdResetCommand.getEmail());
        mailSenderAdapter.sendMailForResetPassword(userPwdResetCommand.getEmail(), token.getValue());
    }

    /***
     * GET으로 받은 토큰을 바탕으로 비밀번호를 바꿀 수 있는 새로운 토큰을 생성
     * @param pwdUpdateTokenCommand
     * @return
     */
    @Override
    public PwdUpdateTokenResponse validateResetTokenForPasswordUpdate(PwdUpdateTokenCommand pwdUpdateTokenCommand) {
        Token updatePasswordToken = passwordResetUseCase.
                validateResetTokenForPasswordUpdate(pwdUpdateTokenCommand.getToken());
        return userDataMapper.tokenToPwdUpdateTokenResponse(updatePasswordToken);
    }

    /***
     * 비밀번호를 바꿀 수 있는 토큰을 바탕으로 유저 비밀번호 변경
     * @param resetAndNewPwdCommand
     */
    @Override
    public void updatePassword(ResetAndNewPwdCommand resetAndNewPwdCommand) {
        UUID userId = passwordResetUseCase.getTokenByUserIdForUpdatePassword(resetAndNewPwdCommand.getToken());
        String encodedPassword = passwordEncoder.encode(resetAndNewPwdCommand.getNewPassword());
        userCommandHandler.updatePassword(encodedPassword, userId);
    }

    /***
     * 사용자의 프로필 이미지를 업데이트. S3에 이미지를 업로드하고 리턴 방식은 s3의 주소 리턴
     * @param uploadUserImageCommand
     * @return
     */
    @Override
    public UploadUserImageResponse updateUserProfileImage(UploadUserImageCommand uploadUserImageCommand) {

        File file = fileGenerator.convertByteArrayToFile(uploadUserImageCommand.getUserId(), uploadUserImageCommand.getFileContent(),
                uploadUserImageCommand.getOriginalFileName());
        String s3ProfileImageUrl = s3BucketAdapter.uploadS3ProfileImage(file);
        User user = userCommandHandler.updateProfileImage(uploadUserImageCommand.getUserId(),
                uploadUserImageCommand.getOriginalFileName(), s3ProfileImageUrl);
        return userDataMapper.userToUploadUserImageResponse(user);
    }

    /**
     * 2단계 인증을 위해서 유저는 인증 방식을 선택. 인증 이메일은 등록된 이메일
     * @param twoFactorEnableCommand
     */
    @Override
    public void create2FASetting(TwoFactorEnableCommand twoFactorEnableCommand) {
        userTwoFactorAuthenticationUseCase.initiateTwoFactorAuth(twoFactorEnableCommand.getUserId()
                ,twoFactorEnableCommand.getTwoFactorAuthMethod());
    }

    /**
     * 일단은 EMAIL 2차인증만 가능하도록 설정
     * 유저 이메일로 온 코드를 인증하면 앞으로 유저의 로그인은 2단계 인증을 필요
     * @param twoFactorEmailVerifyCodeCommand
     */
    @Override
    public void save2FA(TwoFactorEmailVerifyCodeCommand twoFactorEmailVerifyCodeCommand) {
        if (twoFactorEmailVerifyCodeCommand.getTwoFactorAuthMethod().equals(TwoFactorAuthMethod.EMAIL)) {
            userTwoFactorAuthenticationUseCase.verifyTwoFactorAuthByEmail(twoFactorEmailVerifyCodeCommand.getUserId()
                    , twoFactorEmailVerifyCodeCommand.getCode());
        } else {
            throw new NotImplementedException("Other Authentication Method Not Implemented");
        }
    }


    /***
     * 유저 한 명 조회
     * @param userTrackQuery userId
     * @return 해당 userId를 가진 사람의 정보 객체
     */
    @Override
    public TrackUserQueryResponse trackUserQuery(UserTrackQuery userTrackQuery) {
        User user = userQueryHandler.findOneUser(userTrackQuery);
        return userDataMapper.userEntityToUserTrackUserQueryResponse(user);
    }
}
