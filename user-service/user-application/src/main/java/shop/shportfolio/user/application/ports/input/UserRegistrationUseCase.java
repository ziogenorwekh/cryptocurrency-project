package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.event.UserCreatedEvent;

import java.util.UUID;

public interface UserRegistrationUseCase {

    UserCreatedEvent createUser(UserCreateCommand userCreateCommand);

    UUID verifyTempEmailCodeAndCreateUserId(String email, String code);

    void sendTempEmailCodeForCreateUser(UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand);

    void deleteTempEmailCode(String email);
}
