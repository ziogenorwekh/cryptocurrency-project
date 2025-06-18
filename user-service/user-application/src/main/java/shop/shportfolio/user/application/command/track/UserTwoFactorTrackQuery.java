package shop.shportfolio.user.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserTwoFactorTrackQuery {

    private UUID userId;
}
