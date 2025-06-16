package shop.shportfolio.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.TokenRequestType;
import shop.shportfolio.user.application.command.update.UserPwdUpdateTokenCommand;
import shop.shportfolio.user.application.command.update.UserUpdateNewPwdCommand;
import shop.shportfolio.user.application.command.update.UserPwdResetCommand;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.ports.input.PasswordUpdateUseCase;
import shop.shportfolio.user.application.ports.output.jwt.JwtTokenAdapter;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

@Component
public class PasswordUpdateFacade implements PasswordUpdateUseCase {

    private final UserQueryHandler userQueryHandler;
    private final JwtTokenAdapter jwtTokenAdapter;
    private final PasswordEncoder passwordEncoder;
    private final UserCommandHandler userCommandHandler;
    private final MailSenderAdapter mailSenderAdapter;

    @Autowired
    public PasswordUpdateFacade(UserQueryHandler userQueryHandler, JwtTokenAdapter jwtTokenAdapter,
                                PasswordEncoder passwordEncoder, UserCommandHandler userCommandHandler,
                                MailSenderAdapter mailSenderAdapter) {
        this.userQueryHandler = userQueryHandler;
        this.jwtTokenAdapter = jwtTokenAdapter;
        this.passwordEncoder = passwordEncoder;
        this.userCommandHandler = userCommandHandler;
        this.mailSenderAdapter = mailSenderAdapter;
    }

    @Override
    public void sendMailResetPwd(UserPwdResetCommand userPwdResetCommand) {
        if (!userQueryHandler.existsUserByEmail(userPwdResetCommand.getEmail())) {
            throw new UserNotfoundException(String.format("%s's user is notfound.", userPwdResetCommand.getEmail()));
        }
        Token token = jwtTokenAdapter.createResetRequestPwdToken(userPwdResetCommand.getEmail(),
                TokenRequestType.REQUEST_RESET_PASSWORD);
        mailSenderAdapter.sendMailForResetPassword(userPwdResetCommand.getEmail(), token.getValue());
    }
    @Override
    public Token validateResetTokenForPasswordUpdate(String token) {
        Token tokenVO = new Token(token);
        Email email = jwtTokenAdapter.verifyResetPwdToken(tokenVO);
        User user = userQueryHandler.findOneUserByEmail(email.getValue());
        return jwtTokenAdapter.
                createUpdatePasswordToken(user.getId().getValue(), TokenRequestType.REQUEST_UPDATE_PASSWORD);
    }

    @Override
    public void getTokenByUserIdForUpdatePassword(UserUpdateNewPwdCommand userUpdateNewPwdCommand) {
        String userId = jwtTokenAdapter.getUserIdByUpdatePasswordToken(new Token(userUpdateNewPwdCommand.getToken()));
        String encodedPassword = passwordEncoder.encode(userUpdateNewPwdCommand.getNewPassword());
        userCommandHandler.updatePassword(encodedPassword, UUID.fromString(userId));
    }

}
