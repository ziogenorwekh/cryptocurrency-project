package shop.shportfolio.user.application.command.track;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserTrHistoryOneTrackQuery {

    private final UUID userId;
    private final UUID trHistoryId;

    public UserTrHistoryOneTrackQuery(UUID userId, UUID trHistoryId) {
        this.userId = userId;
        this.trHistoryId = trHistoryId;
    }
}
