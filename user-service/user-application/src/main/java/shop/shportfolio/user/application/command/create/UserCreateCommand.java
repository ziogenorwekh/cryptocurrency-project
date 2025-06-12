package shop.shportfolio.user.application.command.create;


import lombok.Getter;

@Getter
public class UserCreateCommand {
    private String username;
    private String phoneNumber;
    private String email;
    private String password;

    public UserCreateCommand(String username, String phoneNumber, String email, String password) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }
}
