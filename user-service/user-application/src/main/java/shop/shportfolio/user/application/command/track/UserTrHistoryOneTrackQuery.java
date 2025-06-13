package shop.shportfolio.user.application.command.track;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserTrHistoryOneTrackQuery {

    private UUID userId;
    private UUID trHistoryId;

    public UserTrHistoryOneTrackQuery(UUID userId, UUID trHistoryId) {
        this.userId = userId;
        this.trHistoryId = trHistoryId;
    }
}
