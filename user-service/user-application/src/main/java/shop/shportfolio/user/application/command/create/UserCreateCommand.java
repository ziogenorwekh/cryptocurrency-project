package shop.shportfolio.user.application.command.create;


import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreateCommand {
    private UUID userId;
    private String username;
    private String phoneNumber;
    private String email;
    private String password;

    public UserCreateCommand(UUID userId, String username,
                             String phoneNumber, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }
}
