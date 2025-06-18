package shop.shportfolio.user.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.user.application.command.auth.LoginCommand;
import shop.shportfolio.user.application.command.auth.LoginResponse;
import shop.shportfolio.user.application.command.auth.LoginTwoFactorCommand;
import shop.shportfolio.user.domain.valueobject.LoginVO;

public interface UserAuthenticationUseCase {


    LoginVO login(LoginCommand loginCommand);

    LoginVO verify2FA(LoginTwoFactorCommand loginTwoFactorCommand);
}
