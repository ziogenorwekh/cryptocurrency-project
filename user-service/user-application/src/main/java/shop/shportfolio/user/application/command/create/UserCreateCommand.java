package shop.shportfolio.user.application.command.create;


import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreateCommand {
    private final UUID userId;
    private final String username;
    private final String phoneNumber;
    private final String email;
    private final String password;

    public UserCreateCommand(UUID userId, String username,
                             String phoneNumber, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }
}
