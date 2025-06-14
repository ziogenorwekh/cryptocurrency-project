package shop.shportfolio.user.application;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.exception.UserAuthExpiredException;
import shop.shportfolio.user.application.exception.UserNotAuthenticationTemporaryEmailException;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.ports.input.UserRegistrationUseCase;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class UserRegistrationFacade implements UserRegistrationUseCase {

    private final RedisAdapter redisAdapter;
    private final AuthCodeGenerator authCodeGenerator;

    public UserRegistrationFacade(RedisAdapter redisAdapter, AuthCodeGenerator authCodeGenerator) {
        this.redisAdapter = redisAdapter;
        this.authCodeGenerator = authCodeGenerator;
    }

    public void isAuthenticatedTempUser(UUID userId, String email) {
        //        커맨드에 유저아이디 및 인증된 이메일과 이름,전화번호,비밀번호 정보
//        캐시에 유저 아이디를 검색하여 존재하면 로직 수행, 존재하지 않으면 예외처리
        if (!redisAdapter.isAuthenticatedTempUserId(userId)) {
            throw new UserNotAuthenticationTemporaryEmailException(String.format("User %s has expired email authentication",
                    email));
        }
        redisAdapter.deleteTempEmailAuthentication(email);
    }

    public UUID verifyTempEmailCodeAndCreateUserId(String email,String code) {
            Boolean isVerified = redisAdapter.verifyTempEmailAuthCode(email,
                    code);
            if (!isVerified) {
                throw new UserAuthExpiredException(String.format("%s's temporal authentication is already expired",
                        email));
            }
            UUID userId = UUID.randomUUID();
            redisAdapter.saveTempUserId(userId, email, 15, TimeUnit.MINUTES);
            return userId;
    }

    public String sendTempEmailCodeForCreateUser(String email) {
        String code = authCodeGenerator.generate();
        redisAdapter.saveTempEmailCode(email, code, 15, TimeUnit.MINUTES);
        return code;
    }
}
