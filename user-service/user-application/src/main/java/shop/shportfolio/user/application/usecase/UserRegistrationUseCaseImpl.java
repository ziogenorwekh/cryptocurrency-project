package shop.shportfolio.user.application.usecase;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.exception.InvalidAuthCodeException;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.UserRegistrationUseCase;
import shop.shportfolio.user.application.ports.output.kafka.UserCreatedPublisher;
import shop.shportfolio.user.application.ports.output.mail.MailSenderPort;
import shop.shportfolio.user.application.ports.output.redis.RedisPort;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderPort;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.event.UserCreatedEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class UserRegistrationUseCaseImpl implements UserRegistrationUseCase {

    private final RedisPort redisPort;
    private final AuthCodeGenerator authCodeGenerator;
    private final PasswordEncoderPort passwordEncoder;
    private final UserCommandHandler userCommandHandler;
    private final MailSenderPort mailSenderPort;
    private final UserCreatedPublisher userCreatedPublisher;

    public UserRegistrationUseCaseImpl(RedisPort redisPort, AuthCodeGenerator authCodeGenerator,
                                       PasswordEncoderPort passwordEncoder, UserCommandHandler userCommandHandler,
                                       MailSenderPort mailSenderPort, UserCreatedPublisher userCreatedPublisher) {
        this.redisPort = redisPort;
        this.authCodeGenerator = authCodeGenerator;
        this.passwordEncoder = passwordEncoder;
        this.userCommandHandler = userCommandHandler;
        this.mailSenderPort = mailSenderPort;
        this.userCreatedPublisher = userCreatedPublisher;
    }

    @Override
    public User createUser(UserCreateCommand userCreateCommand) {
        this.isAuthenticatedTempUser(userCreateCommand.getUserId(), userCreateCommand.getEmail());
        String encryptedPassword = passwordEncoder.encode(userCreateCommand.getPassword());
        User user = userCommandHandler.createUser(userCreateCommand.getUserId(),userCreateCommand.getEmail()
                ,userCreateCommand.getPhoneNumber(),userCreateCommand.getUsername(), encryptedPassword);
        UserCreatedEvent userCreatedEvent = userCommandHandler.createUserCreatedEvent(user.getId());
        userCreatedPublisher.publish(userCreatedEvent);
        this.deleteTempEmailCode(user.getEmail().getValue());
        return user;
    }

    @Override
    public UUID verifyTempEmailCodeAndCreateUserId(String email,String code) {
            Boolean isVerified = redisPort.verifyTempEmailAuthCode(email,
                    code);
            if (!isVerified) {
                throw new InvalidAuthCodeException(String.format("%s's temporal authentication is already expired",
                        email));
            }
            UUID userId = UUID.randomUUID();
            redisPort.saveTempUserId(userId, email, 15, TimeUnit.MINUTES);
            return userId;
    }

    @Override
    public void sendTempEmailCodeForCreateUser(UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand) {
        userCommandHandler.isDuplicatedEmail(userTempEmailAuthRequestCommand.getEmail());
        String code = authCodeGenerator.generate();
        mailSenderPort.sendMailWithEmailAndCode(userTempEmailAuthRequestCommand.getEmail(), code);
        redisPort.saveTempEmailCode(userTempEmailAuthRequestCommand.getEmail(), code, 15, TimeUnit.MINUTES);

    }

    public void deleteTempEmailCode(String email) {
        redisPort.deleteTempEmailCode(email);
    }

    private void isAuthenticatedTempUser(UUID userId, String email) {
        //        커맨드에 유저아이디 및 인증된 이메일과 이름,전화번호,비밀번호 정보
//        캐시에 유저 아이디를 검색하여 존재하면 로직 수행, 존재하지 않으면 예외처리
        if (!redisPort.isAuthenticatedTempUserId(userId)) {
            throw new InvalidAuthCodeException(String.format("User %s has expired email authentication",
                    email));
        }
    }
}
