package shop.shportfolio.user.application.command.track;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserTrHistoryListTrackQuery {


    private UUID userId;

    public UserTrHistoryListTrackQuery(UUID userId) {
        this.userId = userId;
    }
}
