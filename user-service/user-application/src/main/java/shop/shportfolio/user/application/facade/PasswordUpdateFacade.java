package shop.shportfolio.user.application.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.TokenType;
import shop.shportfolio.user.application.command.update.UserUpdateNewPwdCommand;
import shop.shportfolio.user.application.command.update.UserPwdResetCommand;
import shop.shportfolio.user.application.exception.InvalidPasswordException;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.PasswordUpdateUseCase;
import shop.shportfolio.user.application.ports.output.security.JwtTokenAdapter;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderAdapter;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

@Component
public class PasswordUpdateFacade implements PasswordUpdateUseCase {

    private final JwtTokenAdapter jwtTokenAdapter;
    private final PasswordEncoderAdapter passwordEncoder;
    private final UserCommandHandler userCommandHandler;
    private final MailSenderAdapter mailSenderAdapter;

    @Autowired
    public PasswordUpdateFacade(JwtTokenAdapter jwtTokenAdapter, PasswordEncoderAdapter passwordEncoder,
                                UserCommandHandler userCommandHandler,
                                MailSenderAdapter mailSenderAdapter) {
        this.jwtTokenAdapter = jwtTokenAdapter;
        this.passwordEncoder = passwordEncoder;
        this.userCommandHandler = userCommandHandler;
        this.mailSenderAdapter = mailSenderAdapter;
    }

    @Override
    public void requestPasswordResetByEmail(UserPwdResetCommand userPwdResetCommand) {
        userCommandHandler.findUserByEmail(userPwdResetCommand.getEmail());
        String tokenByEmail = jwtTokenAdapter.generateResetTokenByEmail(userPwdResetCommand.getEmail(),
                TokenType.REQUEST_RESET_PASSWORD);
        mailSenderAdapter.sendMailForResetPassword(userPwdResetCommand.getEmail(), tokenByEmail);
    }
    @Override
    public Token verifyResetTokenAndIssueUpdateToken(String token) {
        Token tokenVO = new Token(token);
        Email email = new Email(jwtTokenAdapter.extractEmailFromResetToken(tokenVO));
        User user = userCommandHandler.findUserByEmail(email.getValue());
        String pwdUpdateToken = jwtTokenAdapter.
                createUpdatePasswordToken(user.getId().getValue(), TokenType.REQUEST_UPDATE_PASSWORD);
        return new Token(pwdUpdateToken);
    }

    @Override
    public void updatePasswordWithVerifiedToken(UserUpdateNewPwdCommand userUpdateNewPwdCommand) {
        UUID userId = jwtTokenAdapter.extractUserIdFromUpdateToken(new Token(userUpdateNewPwdCommand.getToken()));
        User user = userCommandHandler.findUserByUserId(userId);
        boolean matches = passwordEncoder.matches(userUpdateNewPwdCommand.getNewPassword(), user.getPassword().getValue());

        if (matches) {
            throw new InvalidPasswordException("password must not match old password");
        }
        String encodedPassword = passwordEncoder.encode(userUpdateNewPwdCommand.getNewPassword());
        userCommandHandler.setNewPasswordAfterReset(encodedPassword, user);
    }

}
