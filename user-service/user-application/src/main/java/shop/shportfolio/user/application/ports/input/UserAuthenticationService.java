package shop.shportfolio.user.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.user.application.command.auth.LoginCommand;
import shop.shportfolio.user.application.command.auth.LoginResponse;
import shop.shportfolio.user.application.command.auth.LoginTwoFactorCommand;


public interface UserAuthenticationService {

    LoginResponse userLogin(@Valid LoginCommand loginCommand);

    LoginResponse userVerify2FACode(@Valid LoginTwoFactorCommand loginTwoFactorCommand);
}
