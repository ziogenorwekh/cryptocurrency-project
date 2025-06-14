package shop.shportfolio.user.application.command.track;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserTrackQuery {

    private final UUID userId;

    public UserTrackQuery(UUID userId) {
        this.userId = userId;
    }
}
