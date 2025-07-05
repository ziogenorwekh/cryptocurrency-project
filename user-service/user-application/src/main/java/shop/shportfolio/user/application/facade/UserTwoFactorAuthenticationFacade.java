package shop.shportfolio.user.application.facade;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.update.TwoFactorEmailVerifyCodeCommand;
import shop.shportfolio.user.application.command.update.TwoFactorEnableCommand;
import shop.shportfolio.user.application.exception.*;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.UserTwoFactorAuthenticationUseCase;
import shop.shportfolio.user.application.ports.output.mail.MailSenderPort;
import shop.shportfolio.user.application.ports.output.redis.RedisPort;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.concurrent.TimeUnit;

@Component
public class UserTwoFactorAuthenticationFacade implements UserTwoFactorAuthenticationUseCase {

    private final RedisPort redisPort;
    private final UserCommandHandler userCommandHandler;
    private final MailSenderPort mailSenderPort;
    private final AuthCodeGenerator authCodeGenerator;

    public UserTwoFactorAuthenticationFacade(RedisPort redisPort, UserCommandHandler userCommandHandler,
                                             MailSenderPort mailSenderPort, AuthCodeGenerator authCodeGenerator) {
        this.redisPort = redisPort;
        this.userCommandHandler = userCommandHandler;
        this.mailSenderPort = mailSenderPort;
        this.authCodeGenerator = authCodeGenerator;
    }

    @Override
    public void initiateTwoFactorAuth(TwoFactorEnableCommand twoFactorEnableCommand) {
        User user = userCommandHandler.findUserByUserId(twoFactorEnableCommand.getUserId());

        switch (twoFactorEnableCommand.getTwoFactorAuthMethod()) {
            case EMAIL -> {
                String generatedCode = authCodeGenerator.generate();
                mailSenderPort.sendMailWithEmailAnd2FACode(user.getEmail().getValue(), generatedCode);
                redisPort.save2FAEmailCode(user.getEmail().getValue(), generatedCode, 5, TimeUnit.MINUTES);
            }
            case OTP -> {
                throw new NotImplementedException("OTP is not yet implemented");
            }
            default -> throw new InvalidPasswordException("Invalid two-factor authentication method");
        }
    }

    @Override
    public void verifyTwoFactorAuthByEmail(TwoFactorEmailVerifyCodeCommand twoFactorEmailVerifyCodeCommand) {
        User user = userCommandHandler.findUserByUserId(twoFactorEmailVerifyCodeCommand.getUserId());
        if (!user.getEmail().getValue().equals(user.getEmail().getValue())) {
            throw new InvalidRequestException("Requested Email address does not match");
        }

        if (!redisPort.isSave2FAEmailCode(user.getEmail().getValue(), twoFactorEmailVerifyCodeCommand.getCode())) {
            throw new InvalidAuthCodeException("2FA code is invalid or expired");
        }

        redisPort.delete2FASettingEmailCode(user.getEmail().getValue());

        userCommandHandler.save2FA(user, TwoFactorAuthMethod.EMAIL);
    }

//    @Override
//    public void send2faCode(String userId, String email) {
//
//    }
//
//    @Override
//    public Boolean verify2faCode(String userId, String code) {
//        return null;
//    }
}
