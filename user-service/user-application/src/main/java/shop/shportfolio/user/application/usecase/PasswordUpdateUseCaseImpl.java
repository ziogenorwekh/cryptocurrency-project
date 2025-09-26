package shop.shportfolio.user.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.TokenType;
import shop.shportfolio.user.application.command.update.UserUpdateNewPwdCommand;
import shop.shportfolio.user.application.command.update.UserPwdResetCommand;
import shop.shportfolio.user.application.exception.InvalidPasswordException;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.PasswordUpdateUseCase;
import shop.shportfolio.user.application.ports.output.security.JwtTokenPort;
import shop.shportfolio.user.application.ports.output.mail.MailSenderPort;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderPort;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

@Component
public class PasswordUpdateUseCaseImpl implements PasswordUpdateUseCase {

    private final JwtTokenPort jwtTokenPort;
    private final PasswordEncoderPort passwordEncoder;
    private final UserCommandHandler userCommandHandler;
    private final MailSenderPort mailSenderPort;

    @Autowired
    public PasswordUpdateUseCaseImpl(JwtTokenPort jwtTokenPort, PasswordEncoderPort passwordEncoder,
                                     UserCommandHandler userCommandHandler,
                                     MailSenderPort mailSenderPort) {
        this.jwtTokenPort = jwtTokenPort;
        this.passwordEncoder = passwordEncoder;
        this.userCommandHandler = userCommandHandler;
        this.mailSenderPort = mailSenderPort;
    }

    @Override
    @Transactional(readOnly = true)
    public void requestPasswordResetByEmail(UserPwdResetCommand userPwdResetCommand) {
        userCommandHandler.findUserByEmail(userPwdResetCommand.getEmail());
        String tokenByEmail = jwtTokenPort.generateResetTokenByEmail(userPwdResetCommand.getEmail(),
                TokenType.REQUEST_RESET_PASSWORD);
        mailSenderPort.sendMailForResetPassword(userPwdResetCommand.getEmail(), tokenByEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public Token verifyResetTokenAndIssueUpdateToken(String token) {
        Token tokenVO = new Token(token);
        Email email = new Email(jwtTokenPort.extractEmailFromResetToken(tokenVO));
        User user = userCommandHandler.findUserByEmail(email.getValue());
        String pwdUpdateToken = jwtTokenPort.
                createUpdatePasswordToken(user.getId().getValue(), TokenType.REQUEST_UPDATE_PASSWORD);
        return new Token(pwdUpdateToken);
    }

    @Override
    @Transactional
    public void updatePasswordWithVerifiedToken(UserUpdateNewPwdCommand userUpdateNewPwdCommand) {
        UUID userId = jwtTokenPort.extractUserIdFromUpdateToken(new Token(userUpdateNewPwdCommand.getToken()));
        User user = userCommandHandler.findUserByUserId(userId);
        boolean matches = passwordEncoder.matches(userUpdateNewPwdCommand.getNewPassword(), user.getPassword().getValue());

        if (matches) {
            throw new InvalidPasswordException("password must not match old password");
        }
        String encodedPassword = passwordEncoder.encode(userUpdateNewPwdCommand.getNewPassword());
        userCommandHandler.setNewPasswordAfterReset(encodedPassword, user);
    }

}
