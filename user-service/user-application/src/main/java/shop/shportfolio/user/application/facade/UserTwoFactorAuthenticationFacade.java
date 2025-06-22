package shop.shportfolio.user.application.facade;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.update.TwoFactorEmailVerifyCodeCommand;
import shop.shportfolio.user.application.command.update.TwoFactorEnableCommand;
import shop.shportfolio.user.application.exception.*;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.UserTwoFactorAuthenticationUseCase;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.concurrent.TimeUnit;

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
    public void initiateTwoFactorAuth(TwoFactorEnableCommand twoFactorEnableCommand) {
        User user = userCommandHandler.findUserByUserId(twoFactorEnableCommand.getUserId());

        switch (twoFactorEnableCommand.getTwoFactorAuthMethod()) {
            case EMAIL -> {
                String generatedCode = authCodeGenerator.generate();
                mailSenderAdapter.sendMailWithEmailAnd2FACode(user.getEmail().getValue(), generatedCode);
                redisAdapter.save2FAEmailCode(user.getEmail().getValue(), generatedCode, 5, TimeUnit.MINUTES);
            }
            case OTP -> {
                throw new NotImplementedException("OTP is not yet implemented");
            }
            default -> throw new InvalidObjectException("Invalid two-factor authentication method");
        }
    }

    @Override
    public void verifyTwoFactorAuthByEmail(TwoFactorEmailVerifyCodeCommand twoFactorEmailVerifyCodeCommand) {
        User user = userCommandHandler.findUserByUserId(twoFactorEmailVerifyCodeCommand.getUserId());
        if (!user.getEmail().getValue().equals(user.getEmail().getValue())) {
            throw new InvalidRequestException("Requested Email address does not match");
        }

        if (!redisAdapter.isSave2FAEmailCode(user.getEmail().getValue(), twoFactorEmailVerifyCodeCommand.getCode())) {
            throw new InvalidAuthCodeException("2FA code is invalid or expired");
        }

        redisAdapter.delete2FASettingEmailCode(user.getEmail().getValue());

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
