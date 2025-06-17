package shop.shportfolio.user.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.command.track.UserTrHistoryListTrackQuery;
import shop.shportfolio.user.application.command.track.UserTrHistoryOneTrackQuery;
import shop.shportfolio.user.application.dto.TransactionHistoryDTO;
import shop.shportfolio.user.application.exception.TransactionHistoryNotfoundException;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.application.ports.output.repository.UserTrHistoryRepositoryAdapter;
import shop.shportfolio.user.domain.TrHistoryDomainService;
import shop.shportfolio.user.domain.entity.TransactionHistory;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Amount;
import shop.shportfolio.user.domain.valueobject.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class UserTrHistoryCommandHandler {


    private final UserTrHistoryRepositoryAdapter userTrHistoryRepositoryAdapter;
    private final UserRepositoryAdaptor userRepositoryAdaptor;
    private final TrHistoryDomainService trHistoryDomainService;

    @Autowired
    public UserTrHistoryCommandHandler(UserTrHistoryRepositoryAdapter userTrHistoryRepositoryAdapter,
                                       UserRepositoryAdaptor userRepositoryAdaptor,
                                       TrHistoryDomainService trHistoryDomainService) {
        this.userTrHistoryRepositoryAdapter = userTrHistoryRepositoryAdapter;
        this.userRepositoryAdaptor = userRepositoryAdaptor;
        this.trHistoryDomainService = trHistoryDomainService;
    }

    public List<TransactionHistory>  findTransactionHistories(
            UUID userId) {
        return userTrHistoryRepositoryAdapter
                .findUserTransactionHistoryByUserId(userId);
    }

    public TransactionHistory findOneTransactionHistory(UUID userId, UUID transactionId) {
        return userTrHistoryRepositoryAdapter.findUserTransactionHistoryByUserIdAndHistoryId(
                        userId, transactionId)
                .orElseThrow(() -> new TransactionHistoryNotfoundException(String.format("%s is not found",
                        userId)));
    }

    /***
     * 카프카가 거래 서비스에서 거래 성공할 경우 해당 메서드로 메세지를 보내서 거래 기록 저장
     * @param userId
     * @param transactionHistoryDTO
     * @return
     */
    public TransactionHistory saveTransactionHistory(UUID userId, TransactionHistoryDTO transactionHistoryDTO) {
        User user = userRepositoryAdaptor.findByUserId(userId).orElseThrow(() -> new UserNotfoundException(
                String.format("User %s is not found", userId)));

        long amount = Long.parseLong(transactionHistoryDTO.getAmount());
        TransactionHistory transactionHistory = trHistoryDomainService.save(new UserId(user.getId().getValue()),
                new MarketId(transactionHistoryDTO.getMarketId()),
                TransactionType.valueOf(transactionHistoryDTO.getTransactionType()),
                new Amount(BigDecimal.valueOf(amount)), transactionHistoryDTO.getTransactionTime());
        return userTrHistoryRepositoryAdapter.save(transactionHistory);
    }
}
