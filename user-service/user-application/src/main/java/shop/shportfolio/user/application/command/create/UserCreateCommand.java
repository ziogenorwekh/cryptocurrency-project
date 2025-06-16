package shop.shportfolio.user.application.command.create;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateCommand {
    private UUID userId;
    private String username;
    private String phoneNumber;
    private String email;
    private String password;

}
