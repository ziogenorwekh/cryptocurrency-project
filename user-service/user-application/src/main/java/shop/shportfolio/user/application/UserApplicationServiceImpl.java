package shop.shportfolio.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.common.domain.valueobject.Token;
import shop.shportfolio.user.application.command.resetpwd.PwdUpdateTokenCommand;
import shop.shportfolio.user.application.command.resetpwd.PwdUpdateTokenResponse;
import shop.shportfolio.user.application.command.resetpwd.ResetAndNewPwdCommand;
import shop.shportfolio.user.application.command.resetpwd.UserPwdResetCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserDuplicationException;
import shop.shportfolio.user.application.facade.PasswordResetFacade;
import shop.shportfolio.user.application.facade.UserRegistrationFacade;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

@Service
@Validated
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserCommandHandler userCommandHandler;
    private final UserDataMapper userDataMapper;
    private final UserQueryHandler userQueryHandler;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderAdapter mailSenderAdapter;
    private final UserRegistrationFacade userRegistrationFacade;
    private final PasswordResetFacade passwordResetFacade;
    @Autowired
    public UserApplicationServiceImpl(UserCommandHandler userCommandHandler,
                                      UserDataMapper userDataMapper, UserQueryHandler userQueryHandler,
                                      PasswordEncoder passwordEncoder,
                                      MailSenderAdapter mailSenderAdapter,
                                      UserRegistrationFacade userRegistrationFacade,
                                      PasswordResetFacade passwordResetFacade) {
        this.userCommandHandler = userCommandHandler;
        this.userDataMapper = userDataMapper;
        this.userQueryHandler = userQueryHandler;
        this.passwordEncoder = passwordEncoder;
        this.userRegistrationFacade = userRegistrationFacade;
        this.mailSenderAdapter = mailSenderAdapter;
        this.passwordResetFacade = passwordResetFacade;
    }


    /***
     * pwdencoder, commandhandler
     * @param userCreateCommand
     * @return
     */
    @Override
    public UserCreatedResponse createUser(UserCreateCommand userCreateCommand) {
        userRegistrationFacade.isAuthenticatedTempUser(userCreateCommand.getUserId(), userCreateCommand.getEmail());
//       비밀번호를 암호화하고, 회원가입 핸들러로 이동하여 엔티티 객체로 리턴
        String encryptedPassword = passwordEncoder.encode(userCreateCommand.getPassword());
        User user = userCommandHandler.createUser(userCreateCommand, encryptedPassword);
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
        String code = userRegistrationFacade.sendTempEmailCodeForCreateUser(userTempEmailAuthRequestCommand.getEmail());
        mailSenderAdapter.sendMailForTempEmailAuth(userTempEmailAuthRequestCommand.getEmail(), code);
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
        UUID userId = userRegistrationFacade.verifyTempEmailCodeAndCreateUserId(userTempEmailAuthVerifyCommand.getEmail(),
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
        Token token = passwordResetFacade.sendMailResetPwd(userPwdResetCommand.getEmail());
        mailSenderAdapter.sendMailForResetPassword(userPwdResetCommand.getEmail(), token.getValue());
    }

    /***
     * GET으로 받은 토큰을 바탕으로 비밀번호를 바꿀 수 있는 새로운 토큰을 생성
     * @param pwdUpdateTokenCommand
     * @return
     */
    @Override
    public PwdUpdateTokenResponse validateResetTokenForPasswordUpdate(PwdUpdateTokenCommand pwdUpdateTokenCommand) {
        Token updatePasswordToken = passwordResetFacade.
                validateResetTokenForPasswordUpdate(pwdUpdateTokenCommand.getToken());
        return userDataMapper.tokenToPwdUpdateTokenResponse(updatePasswordToken);
    }

    /***
     * 비밀번호를 바꿀 수 있는 토큰을 바탕으로 유저 비밀번호 변경
     * @param resetAndNewPwdCommand
     */
    @Override
    public void updatePassword(ResetAndNewPwdCommand resetAndNewPwdCommand) {
        UUID userId = passwordResetFacade.getTokenByUserIdForUpdatePassword(resetAndNewPwdCommand.getToken());
        String encodedPassword = passwordEncoder.encode(resetAndNewPwdCommand.getNewPassword());
        userCommandHandler.updatePassword(encodedPassword, userId);
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
