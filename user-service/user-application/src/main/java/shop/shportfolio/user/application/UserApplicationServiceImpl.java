package shop.shportfolio.user.application;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.user.application.ports.output.kafka.UserCreatedPublisher;
import shop.shportfolio.user.application.ports.output.kafka.UserDeletedPublisher;
import shop.shportfolio.user.domain.event.UserCreatedEvent;
import shop.shportfolio.user.domain.event.UserDeletedEvent;
import shop.shportfolio.user.domain.valueobject.Token;
import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.command.track.TrackUserTwoFactorResponse;
import shop.shportfolio.user.application.command.track.UserTwoFactorTrackQuery;
import shop.shportfolio.user.application.command.update.*;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.NotImplementedException;
import shop.shportfolio.user.application.ports.input.*;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.domain.entity.SecuritySettings;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

@Service
@Validated
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserDataMapper userDataMapper;
    private final UserTrackUseCase  userTrackUseCase;
    private final UserUpdateDeleteUseCase userUpdateDeleteUseCase;
    private final UserRegistrationUseCase userRegistrationUseCase;
    private final PasswordUpdateUseCase passwordUpdateUseCase;
    private final UserTwoFactorAuthenticationUseCase userTwoFactorAuthenticationUseCase;
    private final UserDeletedPublisher userDeletedPublisher;
    private final UserCreatedPublisher userCreatedPublisher;
    @Autowired
    public UserApplicationServiceImpl(UserDataMapper userDataMapper,
                                      UserTrackUseCase userTrackUseCase,
                                      UserUpdateDeleteUseCase userUpdateDeleteUseCase,
                                      UserRegistrationUseCase userRegistrationUseCase,
                                      PasswordUpdateUseCase passwordUpdateUseCase,
                                      UserTwoFactorAuthenticationUseCase userTwoFactorAuthenticationUseCase,
                                      UserDeletedPublisher userDeletedPublisher,
                                      UserCreatedPublisher userCreatedPublisher) {
        this.userDataMapper = userDataMapper;
        this.userTrackUseCase = userTrackUseCase;
        this.userUpdateDeleteUseCase = userUpdateDeleteUseCase;
        this.userRegistrationUseCase = userRegistrationUseCase;
        this.passwordUpdateUseCase = passwordUpdateUseCase;
        this.userTwoFactorAuthenticationUseCase = userTwoFactorAuthenticationUseCase;
        this.userDeletedPublisher = userDeletedPublisher;
        this.userCreatedPublisher = userCreatedPublisher;
    }


    /***
     * pwdencoder, commandhandler
     * @param userCreateCommand 이름, 전화번호, 이메일
     * @return 생성 아이디
     */
    @Override
    public UserCreatedResponse createUser(@Valid UserCreateCommand userCreateCommand) {
//       비밀번호를 암호화하고, 회원가입 핸들러로 이동하여 엔티티 객체로 리턴
        UserCreatedEvent userCreatedEvent = userRegistrationUseCase.createUser(userCreateCommand);
        userCreatedPublisher.publish(userCreatedEvent);
//        도메인 객체를 매퍼로 최종 response 값 리턴
        return userDataMapper.userEntityToUserCreatedResponse(userCreatedEvent.getDomainType());
    }

    /***
     * queryhandler, mailsender
     * 회원가입을 위한 임시 이메일 인증 코드 발송
     * @param userTempEmailAuthRequestCommand 이메일
     * @return 인증할 코드
     */
    @Override
    public void sendTempEmailCodeForCreateUser(
            @Valid UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand) {
        userRegistrationUseCase.sendTempEmailCodeForCreateUser(userTempEmailAuthRequestCommand);
    }


    /***
     *
     * 이메일에서 받은 인증코드를 통해서 캐시에 회원가입이 가능한 제한 시간 저장
     * @param userTempEmailAuthVerifyCommand 이메일과 인증 코드
     * @return 유저 아이디와 유저 이메일
     */
    @Override
    public VerifiedTempEmailUserResponse verifyTempEmailCodeForCreateUser(
            @Valid UserTempEmailAuthVerifyCommand userTempEmailAuthVerifyCommand) {
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
    public void sendMailResetPwd(@Valid UserPwdResetCommand userPwdResetCommand) {
        passwordUpdateUseCase.requestPasswordResetByEmail(userPwdResetCommand);
    }

    /***
     * GET으로 받은 토큰을 바탕으로 비밀번호를 바꿀 수 있는 새로운 토큰을 생성
     * @param token 리셋을 요청하는 값을 가진 토큰
     * @return 비밀번호를 바꿀 수 있는 권한을 가진 토큰
     */
    @Override
    public PwdUpdateTokenResponse validateResetTokenForPasswordUpdate(String token) {
        Token updatePasswordToken = passwordUpdateUseCase.
                verifyResetTokenAndIssueUpdateToken(token);
        return userDataMapper.tokenToPwdUpdateTokenResponse(updatePasswordToken);
    }

    /***
     * 비밀번호를 바꿀 수 있는 토큰을 바탕으로 유저 비밀번호 변경
     * @param userUpdateNewPwdCommand 새로운 비밀번호,
     */
    @Override
    public void setNewPasswordAfterReset(@Valid UserUpdateNewPwdCommand userUpdateNewPwdCommand) {
        passwordUpdateUseCase.updatePasswordWithVerifiedToken(userUpdateNewPwdCommand);
    }

    /***
     * 사용자의 프로필 이미지를 업데이트. S3에 이미지를 업로드하고 리턴 방식은 s3의 주소 리턴
     * @param uploadUserImageCommand 유저 이미지 파일 이름과 데이터 byte[] 배열
     * @return S3 URL 리턴
     */
    @Override
    public UploadUserImageResponse updateUserProfileImage(@Valid UploadUserImageCommand uploadUserImageCommand) {
        User user = userUpdateDeleteUseCase.uploadImage(uploadUserImageCommand);
        return userDataMapper.userToUploadUserImageResponse(user);
    }

    /**
     * 일단은 EMAIL 2차인증만 가능하도록 설정
     * 유저 이메일로 온 코드를 인증하면 앞으로 유저의 로그인은 2단계 인증을 필요
     * @param twoFactorEmailVerifyCodeCommand 2단계 인증방식과 인증 코드
     */
    @Override
    public void save2FA(@Valid TwoFactorEmailVerifyCodeCommand twoFactorEmailVerifyCodeCommand) {
        if (twoFactorEmailVerifyCodeCommand.getTwoFactorAuthMethod().equals(TwoFactorAuthMethod.EMAIL)) {
            userTwoFactorAuthenticationUseCase.verifyTwoFactorAuthByEmail(twoFactorEmailVerifyCodeCommand);
        } else {
            throw new NotImplementedException("Other Authentication Method Not Implemented");
        }
    }

    /**
     * 2단계 인증을 위해서 유저는 인증 방식을 선택. 인증 이메일은 등록된 이메일
     * @param twoFactorEnableCommand 2단계 인증 방식 설정
     */
    @Override
    public void create2FASetting(@Valid TwoFactorEnableCommand twoFactorEnableCommand) {
        userTwoFactorAuthenticationUseCase.initiateTwoFactorAuth(twoFactorEnableCommand);
    }

    /**
     * 유저 아이디 조회 및 삭제(거래내역은 삭제하지 않음)
     * @param userDeleteCommand userId
     */
    @Override
    public void deleteUser(@Valid UserDeleteCommand userDeleteCommand) {
        UserDeletedEvent userDeletedEvent = userUpdateDeleteUseCase.deleteUser(userDeleteCommand);
        userDeletedPublisher.publish(userDeletedEvent);
    }

    /**
     * 유저 2단계 인증정보 조회
     * @param userTwoFactorTrackQuery userId
     * @return 2단계 인증 정보 및 유저 아이디 반환
     */
    @Override
    public TrackUserTwoFactorResponse trackUserTwoFactorQuery(@Valid UserTwoFactorTrackQuery userTwoFactorTrackQuery) {
        SecuritySettings securitySettings = userTrackUseCase.trackUserTwoFactor(userTwoFactorTrackQuery);
        return userDataMapper.SecuritySettingsToTrackUserTwoFactorResponse(securitySettings,
                userTwoFactorTrackQuery.getUserId());
    }

    /**
     * 유저 2단계 인은 삭제
     * @param twoFactorDisableCommand userId
     */
    @Override
    public void disableTwoFactorMethod(@Valid TwoFactorDisableCommand twoFactorDisableCommand) {
        userUpdateDeleteUseCase.disableTwoFactorMethod(twoFactorDisableCommand);
    }

    @Override
    public void updatePasswordWithCurrent(@Valid UserOldPasswordChangeCommand userOldPasswordChangeCommand) {
        userUpdateDeleteUseCase.updateOldPasswordToNewPassword(userOldPasswordChangeCommand);
    }


    /**
     * 유저 한 명 조회
     * @param userTrackQuery userId
     * @return 해당 userId를 가진 사람의 정보 객체
     */
    @Override
    public TrackUserQueryResponse trackUserQuery(@Valid UserTrackQuery userTrackQuery) {
        User user = userTrackUseCase.trackUser(userTrackQuery);
        return userDataMapper.userEntityToUserTrackUserQueryResponse(user);
    }
}
