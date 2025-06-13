package shop.shportfolio.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthenticationResponse;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserAuthExpiredException;
import shop.shportfolio.user.application.exception.UserDuplicationException;
import shop.shportfolio.user.application.exception.UserNotAuthenticationTemporaryEmailException;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.output.cache.CacheAdapter;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

@Service
@Validated
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserCommandHandler userCommandHandler;
    private final UserDataMapper userDataMapper;
    private final UserQueryHandler userQueryHandler;
    private final PasswordEncoder passwordEncoder;
    private final CacheAdapter cacheAdapter;
    @Autowired
    public UserApplicationServiceImpl(UserCommandHandler userCommandHandler, CacheAdapter cacheAdapter,
                                      UserDataMapper userDataMapper, UserQueryHandler userQueryHandler,
                                      PasswordEncoder passwordEncoder) {
        this.userCommandHandler = userCommandHandler;
        this.userDataMapper = userDataMapper;
        this.userQueryHandler = userQueryHandler;
        this.passwordEncoder = passwordEncoder;
        this.cacheAdapter = cacheAdapter;
    }


    @Override
    public UserCreatedResponse createUser(UserCreateCommand userCreateCommand) {
//        커맨드에 유저아이디 및 인증된 이메일과 이름,전화번호,비밀번호 정보
//        캐시에 유저 아이디를 검색하여 존재하면 로직 수행, 존재하지 않으면 예외처리
        if (!cacheAdapter.isAuthenticatedUserId(userCreateCommand.getUserId())) {
            throw new UserNotAuthenticationTemporaryEmailException(String.format("User %s has expired email authentication",
                    userCreateCommand.getEmail()));
        }
//       비밀번호를 암호화하고, 회원가입 핸들러로 이동하여 엔티티 객체로 리턴
        String encryptedPassword = passwordEncoder.encode(userCreateCommand.getPassword());
        User user = userCommandHandler.createUser(userCreateCommand, encryptedPassword);

//        도메인 객체를 매퍼로 최종 response 값 리턴
        return userDataMapper.userEntityToUserCreatedResponse(user);
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

    /***
     * 회원가입을 위한 임시 이메일 인증 코드 발송
     * @param userTempEmailAuthRequestCommand 이메일
     * @return 인증할 코드
     */
    @Override
    public UserTempEmailAuthenticationResponse sendTempEmailCodeForCreateUser(
            UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand) {
        if (userQueryHandler.existsUserByEmail(userTempEmailAuthRequestCommand.getEmail())) {
            throw new UserDuplicationException(String.format("User with email %s already exists",
                    userTempEmailAuthRequestCommand.getEmail()));
        }
        String code = cacheAdapter.saveTempEmailCode(userTempEmailAuthRequestCommand.getEmail());
        return userDataMapper.valueToUserTempEmailAuthenticationResponse(code);
    }

    /***
     * 이메일에서 받은 인증코드를 통해서 캐시에 등록 시간 저장
     * @param userTempEmailAuthVerifyCommand 이메일과 인증 코드
     * @return 유저 아이디와 유저 이메일
     */
    @Override
    public VerifiedTempEmailUserResponse verifyTempEmailCodeForCreateUser(
            UserTempEmailAuthVerifyCommand userTempEmailAuthVerifyCommand) {
        Boolean isVerified = cacheAdapter.verifyTempEmailAuthCode(userTempEmailAuthVerifyCommand.getEmail(),
                userTempEmailAuthVerifyCommand.getCode());
        if (!isVerified) {
            throw new UserAuthExpiredException(String.format("%s is already expired",
                    userTempEmailAuthVerifyCommand.getEmail()));
        }
        UUID userId = UUID.randomUUID();
        cacheAdapter.saveTempUserId(userId,userTempEmailAuthVerifyCommand.getEmail());
        return userDataMapper.valueToVerifiedTempEmailUserResponse(userId,userTempEmailAuthVerifyCommand.getEmail());
    }
}
