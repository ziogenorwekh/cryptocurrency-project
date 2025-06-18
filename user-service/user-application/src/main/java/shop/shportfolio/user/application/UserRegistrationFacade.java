package shop.shportfolio.user.application;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.exception.InvalidAuthCodeException;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.UserRegistrationUseCase;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderAdapter;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class UserRegistrationFacade implements UserRegistrationUseCase {

    private final RedisAdapter redisAdapter;
    private final AuthCodeGenerator authCodeGenerator;
    private final PasswordEncoderAdapter  passwordEncoder;
    private final UserCommandHandler userCommandHandler;
    private final MailSenderAdapter mailSenderAdapter;

    public UserRegistrationFacade(RedisAdapter redisAdapter, AuthCodeGenerator authCodeGenerator,
                                  PasswordEncoderAdapter passwordEncoder, UserCommandHandler userCommandHandler,
                                  MailSenderAdapter mailSenderAdapter) {
        this.redisAdapter = redisAdapter;
        this.authCodeGenerator = authCodeGenerator;
        this.passwordEncoder = passwordEncoder;
        this.userCommandHandler = userCommandHandler;
        this.mailSenderAdapter = mailSenderAdapter;
    }

    @Override
    public User createUser(UserCreateCommand userCreateCommand) {
        this.isAuthenticatedTempUser(userCreateCommand.getUserId(), userCreateCommand.getEmail());
        String encryptedPassword = passwordEncoder.encode(userCreateCommand.getPassword());
        User user = userCommandHandler.createUser(userCreateCommand.getUserId(),userCreateCommand.getEmail()
                ,userCreateCommand.getPhoneNumber(),userCreateCommand.getUsername(), encryptedPassword);
        this.deleteTempEmailCode(user.getEmail().getValue());
        return user;
    }

    @Override
    public UUID verifyTempEmailCodeAndCreateUserId(String email,String code) {
            Boolean isVerified = redisAdapter.verifyTempEmailAuthCode(email,
                    code);
            if (!isVerified) {
                throw new InvalidAuthCodeException(String.format("%s's temporal authentication is already expired",
                        email));
            }
            UUID userId = UUID.randomUUID();
            redisAdapter.saveTempUserId(userId, email, 15, TimeUnit.MINUTES);
            return userId;
    }

    @Override
    public void sendTempEmailCodeForCreateUser(UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand) {
        userCommandHandler.isDuplicatedEmail(userTempEmailAuthRequestCommand.getEmail());
        String code = authCodeGenerator.generate();
        mailSenderAdapter.sendMailWithEmailAndCode(userTempEmailAuthRequestCommand.getEmail(), code);
        redisAdapter.saveTempEmailCode(userTempEmailAuthRequestCommand.getEmail(), code, 15, TimeUnit.MINUTES);

    }

    public void deleteTempEmailCode(String email) {
        redisAdapter.deleteTempEmailCode(email);
    }

    private void isAuthenticatedTempUser(UUID userId, String email) {
        //        커맨드에 유저아이디 및 인증된 이메일과 이름,전화번호,비밀번호 정보
//        캐시에 유저 아이디를 검색하여 존재하면 로직 수행, 존재하지 않으면 예외처리
        if (!redisAdapter.isAuthenticatedTempUserId(userId)) {
            throw new InvalidAuthCodeException(String.format("User %s has expired email authentication",
                    email));
        }
    }
}
