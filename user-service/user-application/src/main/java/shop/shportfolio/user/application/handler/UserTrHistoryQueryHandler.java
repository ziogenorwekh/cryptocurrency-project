package shop.shportfolio.user.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.command.track.UserTrHistoryListTrackQuery;
import shop.shportfolio.user.application.command.track.UserTrHistoryOneTrackQuery;
import shop.shportfolio.user.application.exception.TransactionHistoryNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserTrHistoryRepositoryAdapter;
import shop.shportfolio.user.domain.entity.TransactionHistory;

import java.util.List;

@Component
public class UserTrHistoryQueryHandler {


    private final UserTrHistoryRepositoryAdapter userTrHistoryRepositoryAdapter;

    @Autowired
    public UserTrHistoryQueryHandler(UserTrHistoryRepositoryAdapter userTrHistoryRepositoryAdapter) {
        this.userTrHistoryRepositoryAdapter = userTrHistoryRepositoryAdapter;
    }

    public List<TransactionHistory>  findTransactionHistories(
            UserTrHistoryListTrackQuery userTrHistoryListTrackQuery) {
        return userTrHistoryRepositoryAdapter
                .findUserTransactionHistoryByUserId(userTrHistoryListTrackQuery.getUserId());
    }

    public TransactionHistory findOneTransactionHistory(
            UserTrHistoryOneTrackQuery userTrHistoryOneTrackQuery) {
        return userTrHistoryRepositoryAdapter.findUserTransactionHistoryByUserIdAndHistoryId(
                        userTrHistoryOneTrackQuery.getUserId(), userTrHistoryOneTrackQuery.getTrHistoryId())
                .orElseThrow(() -> new TransactionHistoryNotfoundException(String.format("%s is not found",
                        userTrHistoryOneTrackQuery.getUserId())));
    }
}
