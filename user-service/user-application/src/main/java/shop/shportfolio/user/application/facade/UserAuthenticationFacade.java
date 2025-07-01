package shop.shportfolio.user.application.facade;

import shop.shportfolio.user.application.command.auth.LoginCommand;
import shop.shportfolio.user.application.command.auth.LoginTwoFactorCommand;
import shop.shportfolio.user.application.exception.InvalidAuthCodeException;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.ports.input.UserAuthenticationUseCase;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.application.ports.output.security.AuthenticatorPort;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.domain.entity.Role;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.LoginVO;
import shop.shportfolio.common.domain.valueobject.TokenType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UserAuthenticationFacade implements UserAuthenticationUseCase {

    private final AuthenticatorPort authenticatorPort;
    private final MailSenderAdapter mailSenderAdapter;
    private final UserQueryHandler userQueryHandler;
    private final AuthCodeGenerator authCodeGenerator;
    private final RedisAdapter redisAdapter;

    public UserAuthenticationFacade(AuthenticatorPort authenticatorPort, MailSenderAdapter mailSenderAdapter,
                                    UserQueryHandler userQueryHandler, AuthCodeGenerator authCodeGenerator,
                                    RedisAdapter redisAdapter) {
        this.authenticatorPort = authenticatorPort;
        this.mailSenderAdapter = mailSenderAdapter;
        this.userQueryHandler = userQueryHandler;
        this.authCodeGenerator = authCodeGenerator;
        this.redisAdapter = redisAdapter;
    }

    @Override
    public LoginVO login(LoginCommand loginCommand) {
        UUID userId = authenticatorPort.authenticate(loginCommand.getEmail(), loginCommand.getPassword());
        User user = userQueryHandler.findOneUser(userId);

        if (user.getSecuritySettings().getIsEnabled()) {
            String generated = authCodeGenerator.generate();
            mailSenderAdapter.sendMailWithEmailAnd2FACode(user.getEmail().getValue(), generated);
            String tempToken = authenticatorPort.generate2FATmpToken(user.getEmail().getValue());
            redisAdapter.save2FALoginCode(loginCommand.getEmail(), generated, 3, TimeUnit.MINUTES);
            return new LoginVO(userId, tempToken, TokenType.REQUIRE_2FA);
        } else {
            String token = authenticatorPort.generateLoginToken(userId, user.getRoles());
            return new LoginVO(userId, token, TokenType.COMPLETED);
        }
    }

    @Override
    public LoginVO verify2FA(
            LoginTwoFactorCommand loginTwoFactorCommand) {
        // 유효한 토큰값인지만 확인
        String email = authenticatorPort.getEmailBy2FATmpToken(loginTwoFactorCommand.getTempToken());
        if (!redisAdapter.isSave2FALoginCode(email, loginTwoFactorCommand.getCode())) {
            throw new InvalidAuthCodeException(String.format("%s's temporal authentication is already expired",
                    email));
        }
        User user = userQueryHandler.findOneUserByEmail(email);
        String accessToken = authenticatorPort.generateLoginToken(user.getId().getValue(),user.getRoles());
        redisAdapter.delete2FALoginCode(email);
        return new LoginVO(user.getId().getValue(), accessToken, TokenType.COMPLETED);
    }
}
