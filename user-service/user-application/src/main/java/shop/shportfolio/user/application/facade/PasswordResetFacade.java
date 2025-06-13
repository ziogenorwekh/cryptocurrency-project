package shop.shportfolio.user.application.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.ports.output.jwt.JwtTokenAdapter;

@Component
public class PasswordResetFacade {

    private final UserQueryHandler userQueryHandler;
    private final JwtTokenAdapter jwtTokenAdapter;

    @Autowired
    public PasswordResetFacade(UserQueryHandler userQueryHandler, JwtTokenAdapter jwtTokenAdapter) {
        this.userQueryHandler = userQueryHandler;
        this.jwtTokenAdapter = jwtTokenAdapter;
    }


}
