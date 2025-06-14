package shop.shportfolio.user.application;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.exception.InvalidObjectException;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.UserTwoFactorAuthenticationUseCase;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

@Component
public class UserTwoFactorAuthenticationFacade implements UserTwoFactorAuthenticationUseCase {

    private final RedisAdapter redisAdapter;
    private final UserCommandHandler userCommandHandler;
    private final MailSenderAdapter mailSenderAdapter;
    private final AuthCodeGenerator authCodeGenerator;

    public UserTwoFactorAuthenticationFacade(RedisAdapter redisAdapter, UserCommandHandler userCommandHandler,
                                             MailSenderAdapter mailSenderAdapter, AuthCodeGenerator authCodeGenerator) {
        this.redisAdapter = redisAdapter;
        this.userCommandHandler = userCommandHandler;
        this.mailSenderAdapter = mailSenderAdapter;
        this.authCodeGenerator = authCodeGenerator;
    }

    @Override
    public void update2FASetting(UUID userId, TwoFactorAuthMethod twoFactorAuthMethod) {
        User user = userCommandHandler.findUserByUserId(userId);

        switch (twoFactorAuthMethod) {
            case EMAIL -> {
                String generatedCode = authCodeGenerator.generate();
                mailSenderAdapter.sendMailWithEmailAndCode(user.getEmail().getValue(), generatedCode);
                redisAdapter.saveTempEmailCode()
            }
            case OTP -> {
            }
            default -> throw new InvalidObjectException("Invalid two-factor authentication method");
        }
    }

    @Override
    public void send2faCode(String userId, String email) {

    }

    @Override
    public Boolean verify2faCode(String userId, String code) {
        return null;
    }
}
