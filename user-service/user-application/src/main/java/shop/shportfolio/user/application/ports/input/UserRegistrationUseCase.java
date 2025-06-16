package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.domain.entity.User;

import java.util.UUID;

public interface UserRegistrationUseCase {

    User createUser(UserCreateCommand userCreateCommand);

    UUID verifyTempEmailCodeAndCreateUserId(String email, String code);

    void sendTempEmailCodeForCreateUser(UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand);

    void deleteTempEmailCode(String email);
}
