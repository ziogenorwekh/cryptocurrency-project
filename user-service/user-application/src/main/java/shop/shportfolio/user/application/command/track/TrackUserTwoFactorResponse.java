package shop.shportfolio.user.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TrackUserTwoFactorResponse {


    private final UUID userId;
    private final String twoFactorAuthMethod;
    private final boolean isEnabled;
}
