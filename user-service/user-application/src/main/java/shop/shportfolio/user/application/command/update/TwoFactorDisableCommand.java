package shop.shportfolio.user.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorDisableCommand {

    private UUID userId;
}
