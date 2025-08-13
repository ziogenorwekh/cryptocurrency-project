package shop.shportfolio.user.application;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.user.application.command.auth.LoginCommand;
import shop.shportfolio.user.application.command.auth.LoginResponse;
import shop.shportfolio.user.application.command.auth.LoginTwoFactorCommand;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.input.UserAuthenticationService;
import shop.shportfolio.user.application.ports.input.UserAuthenticationUseCase;
import shop.shportfolio.user.domain.valueobject.LoginVO;

@Service
@Validated
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserAuthenticationUseCase  userAuthenticationUseCase;
    private final UserDataMapper userDataMapper;

    public UserAuthenticationServiceImpl(UserAuthenticationUseCase userAuthenticationUseCase,
                                         UserDataMapper userDataMapper) {
        this.userAuthenticationUseCase = userAuthenticationUseCase;
        this.userDataMapper = userDataMapper;
    }

    @Override
    public LoginResponse userLogin(@Valid LoginCommand loginCommand) {
        LoginVO loginVO = userAuthenticationUseCase.login(loginCommand);
        return userDataMapper.loginVOToLoginResponse(loginVO);
    }

    @Override
    public LoginResponse userVerify2FACode(@Valid LoginTwoFactorCommand loginTwoFactorCommand) {
        LoginVO loginVO = userAuthenticationUseCase.verify2FA(loginTwoFactorCommand);
        return userDataMapper.loginVOToLoginResponse(loginVO);
    }

}
