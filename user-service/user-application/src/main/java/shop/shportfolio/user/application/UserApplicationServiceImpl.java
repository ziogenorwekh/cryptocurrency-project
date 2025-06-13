package shop.shportfolio.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserNotAuthenticationTemporaryEmailException;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.mapper.UserApplicationMapper;
import shop.shportfolio.user.application.ports.output.cache.CacheAdapter;
import shop.shportfolio.user.domain.entity.User;

@Service
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserCommandHandler userCommandHandler;
    private final UserApplicationMapper userApplicationMapper;
    private final UserQueryHandler userQueryHandler;
    private final PasswordEncoder passwordEncoder;
    private final CacheAdapter cacheAdapter;
    @Autowired
    public UserApplicationServiceImpl(UserCommandHandler userCommandHandler, CacheAdapter cacheAdapter,
                                      UserApplicationMapper userApplicationMapper, UserQueryHandler userQueryHandler,
                                      PasswordEncoder passwordEncoder) {
        this.userCommandHandler = userCommandHandler;
        this.userApplicationMapper = userApplicationMapper;
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
        return userApplicationMapper.userEntityToUserCreatedResponse(user);
    }

    @Override
    public TrackUserQueryResponse trackUserQuery(UserTrackQuery userTrackQuery) {
        User user = userQueryHandler.findOneUser(userTrackQuery);

        return userApplicationMapper.userEntityToUserTrackUserQueryResponse(user);
    }
}
