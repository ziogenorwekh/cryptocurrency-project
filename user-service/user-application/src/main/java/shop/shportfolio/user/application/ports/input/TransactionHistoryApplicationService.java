package shop.shportfolio.user.application.ports.input;


import jakarta.validation.Valid;
import shop.shportfolio.user.application.command.track.TrackUserTrHistoryQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrHistoryListTrackQuery;
import shop.shportfolio.user.application.command.track.UserTrHistoryOneTrackQuery;

public interface TransactionHistoryApplicationService {

    TrackUserTrHistoryQueryResponse findTransactionHistories(
            @Valid UserTrHistoryListTrackQuery userTrHistoryListTrackQuery);

    TrackUserTrHistoryQueryResponse findOneTransactionHistory(
            @Valid UserTrHistoryOneTrackQuery trHistoryOneTrackQuery);
}
