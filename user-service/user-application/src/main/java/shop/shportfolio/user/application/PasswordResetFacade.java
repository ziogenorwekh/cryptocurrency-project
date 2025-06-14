package shop.shportfolio.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.TokenRequestType;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.ports.input.PasswordResetUseCase;
import shop.shportfolio.user.application.ports.output.jwt.JwtTokenAdapter;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

@Component
public class PasswordResetFacade implements PasswordResetUseCase {

    private final UserQueryHandler userQueryHandler;
    private final JwtTokenAdapter jwtTokenAdapter;

    @Autowired
    public PasswordResetFacade(UserQueryHandler userQueryHandler, JwtTokenAdapter jwtTokenAdapter) {
        this.userQueryHandler = userQueryHandler;
        this.jwtTokenAdapter = jwtTokenAdapter;
    }


    public Token sendMailResetPwd(String email) {
        if (!userQueryHandler.existsUserByEmail(email)) {
            throw new UserNotfoundException(String.format("%s's user is notfound.", email));
        }
        return jwtTokenAdapter.createResetRequestPwdToken(email,
                TokenRequestType.REQUEST_RESET_PASSWORD);
    }

    public Token validateResetTokenForPasswordUpdate(String pwdUpdateToken) {
        Token token = new Token(pwdUpdateToken);
        Email email = jwtTokenAdapter.verifyResetPwdToken(token);
        User user = userQueryHandler.findOneUserByEmail(email.getValue());
        return jwtTokenAdapter.
                createUpdatePasswordToken(user.getId().getValue(), TokenRequestType.REQUEST_UPDATE_PASSWORD);
    }

    public UUID getTokenByUserIdForUpdatePassword(String token) {
        String userId = jwtTokenAdapter.getUserIdByUpdatePasswordToken(new Token(token));
        return UUID.fromString(userId);
    }

}
