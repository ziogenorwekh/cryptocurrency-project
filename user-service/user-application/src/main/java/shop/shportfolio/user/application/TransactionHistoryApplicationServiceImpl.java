package shop.shportfolio.user.application;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.user.application.command.track.TrackUserTrHistoryQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrHistoryListTrackQuery;
import shop.shportfolio.user.application.command.track.UserTrHistoryOneTrackQuery;
import shop.shportfolio.user.application.handler.UserTrHistoryCommandHandler;
import shop.shportfolio.user.application.mapper.UserDataMapper;
import shop.shportfolio.user.application.ports.input.TransactionHistoryApplicationService;
import shop.shportfolio.user.domain.entity.TransactionHistory;

import java.util.List;

@Service
@Validated
public class TransactionHistoryApplicationServiceImpl implements TransactionHistoryApplicationService {

    private final UserTrHistoryCommandHandler userTrHistoryCommandHandler;
    private final UserDataMapper userDataMapper;
    public TransactionHistoryApplicationServiceImpl(UserTrHistoryCommandHandler userTrHistoryCommandHandler,
                                                    UserDataMapper userDataMapper) {
        this.userTrHistoryCommandHandler = userTrHistoryCommandHandler;
        this.userDataMapper = userDataMapper;
    }

    @Override
    public TrackUserTrHistoryQueryResponse findTransactionHistories(UserTrHistoryListTrackQuery
                                                                                     userTrHistoryListTrackQuery) {
        List<TransactionHistory> transactionHistories = userTrHistoryCommandHandler.
                findTransactionHistories(userTrHistoryListTrackQuery);
        return userDataMapper.listToTrackUserTransactionHistoryQueryResponse(transactionHistories);
    }

    @Override
    public TrackUserTrHistoryQueryResponse findOneTransactionHistory(UserTrHistoryOneTrackQuery trHistoryOneTrackQuery) {
        TransactionHistory oneTransactionHistory = userTrHistoryCommandHandler
                .findOneTransactionHistory(trHistoryOneTrackQuery);
        return userDataMapper.transactionHistoryToTrackUserTransactionHistoryQueryResponse(oneTransactionHistory);
    }
}
