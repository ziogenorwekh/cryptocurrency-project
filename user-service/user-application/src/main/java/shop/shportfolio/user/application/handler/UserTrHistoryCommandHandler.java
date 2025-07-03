package shop.shportfolio.user.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.user.application.dto.TransactionHistoryDTO;
import shop.shportfolio.user.application.exception.TransactionHistoryNotfoundException;
import shop.shportfolio.user.application.exception.UserNotfoundException;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryPort;
import shop.shportfolio.user.application.ports.output.repository.UserTrHistoryRepositoryPort;
import shop.shportfolio.user.domain.TrHistoryDomainService;
import shop.shportfolio.user.domain.entity.TransactionHistory;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.TransactionTime;

import java.util.List;
import java.util.UUID;

@Component
public class UserTrHistoryCommandHandler {


    private final UserTrHistoryRepositoryPort userTrHistoryRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final TrHistoryDomainService trHistoryDomainService;

    @Autowired
    public UserTrHistoryCommandHandler(UserTrHistoryRepositoryPort userTrHistoryRepositoryPort,
                                       UserRepositoryPort userRepositoryPort,
                                       TrHistoryDomainService trHistoryDomainService) {
        this.userTrHistoryRepositoryPort = userTrHistoryRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.trHistoryDomainService = trHistoryDomainService;
    }

    public List<TransactionHistory>  findTransactionHistories(
            UUID userId) {
        return userTrHistoryRepositoryPort
                .findUserTransactionHistoryByUserId(userId);
    }

    public TransactionHistory findOneTransactionHistory(UUID userId, UUID transactionId) {
        return userTrHistoryRepositoryPort.findUserTransactionHistoryByUserIdAndHistoryId(
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
        User user = userRepositoryPort.findByUserId(userId).orElseThrow(() -> new UserNotfoundException(
                String.format("User %s is not found", userId)));

        TransactionHistory transactionHistory = trHistoryDomainService.save(
                new UserId(transactionHistoryDTO.getUserId()),new OrderId(transactionHistoryDTO.getOrderId()),
                new MarketId(transactionHistoryDTO.getMarketId()),transactionHistoryDTO.getTransactionType(),
                new OrderPrice(transactionHistoryDTO.getOrderPrice()),new Quantity(transactionHistoryDTO.getQuantity())
                ,new TransactionTime(transactionHistoryDTO.getTransactionTime())
        );
        return userTrHistoryRepositoryPort.save(transactionHistory);
    }
}
