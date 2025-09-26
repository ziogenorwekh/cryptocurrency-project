package shop.shportfolio.user.application.usecase;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.user.application.command.auth.LoginCommand;
import shop.shportfolio.user.application.command.auth.LoginTwoFactorCommand;
import shop.shportfolio.user.application.exception.InvalidAuthCodeException;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.ports.input.UserAuthenticationUseCase;
import shop.shportfolio.user.application.ports.output.redis.RedisPort;
import shop.shportfolio.user.application.ports.output.security.AuthenticatorPort;
import shop.shportfolio.user.application.ports.output.mail.MailSenderPort;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.LoginVO;
import shop.shportfolio.common.domain.valueobject.TokenType;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class UserAuthenticationUseCaseImpl implements UserAuthenticationUseCase {

    private final AuthenticatorPort authenticatorPort;
    private final MailSenderPort mailSenderPort;
    private final UserQueryHandler userQueryHandler;
    private final AuthCodeGenerator authCodeGenerator;
    private final RedisPort redisPort;

    public UserAuthenticationUseCaseImpl(AuthenticatorPort authenticatorPort, MailSenderPort mailSenderPort,
                                         UserQueryHandler userQueryHandler, AuthCodeGenerator authCodeGenerator,
                                         RedisPort redisPort) {
        this.authenticatorPort = authenticatorPort;
        this.mailSenderPort = mailSenderPort;
        this.userQueryHandler = userQueryHandler;
        this.authCodeGenerator = authCodeGenerator;
        this.redisPort = redisPort;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginVO login(LoginCommand loginCommand) {
        UUID userId = authenticatorPort.authenticate(loginCommand.getEmail(), loginCommand.getPassword());
        User user = userQueryHandler.findOneUser(userId);

        if (user.getSecuritySettings().getIsEnabled()) {
            String generated = authCodeGenerator.generate();
            mailSenderPort.sendMailWithEmailAnd2FACode(user.getEmail().getValue(), generated);
            String tempToken = authenticatorPort.generate2FATmpToken(user.getEmail().getValue());
            redisPort.save2FALoginCode(loginCommand.getEmail(), generated, 3, TimeUnit.MINUTES);
            return new LoginVO(userId, tempToken,user.getEmail().getValue(), TokenType.REQUIRE_2FA);
        } else {
            String token = authenticatorPort.generateLoginToken(userId, user.getRoles());
            return new LoginVO(userId, token, user.getEmail().getValue(),TokenType.COMPLETED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LoginVO verify2FA(
            LoginTwoFactorCommand loginTwoFactorCommand) {
        // 유효한 토큰값인지만 확인
        String email = authenticatorPort.getEmailBy2FATmpToken(loginTwoFactorCommand.getTempToken());
        if (!redisPort.isSave2FALoginCode(email, loginTwoFactorCommand.getCode())) {
            throw new InvalidAuthCodeException(String.format("%s's temporal authentication is already expired",
                    email));
        }
        User user = userQueryHandler.findOneUserByEmail(email);
        String accessToken = authenticatorPort.generateLoginToken(user.getId().getValue(),user.getRoles());
        redisPort.delete2FALoginCode(email);
        return new LoginVO(user.getId().getValue(), user.getEmail().getValue(), accessToken, TokenType.COMPLETED);
    }
}
