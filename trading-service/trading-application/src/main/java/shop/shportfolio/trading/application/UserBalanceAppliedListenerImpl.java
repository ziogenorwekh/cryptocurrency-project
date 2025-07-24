package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.dto.userbalance.UserBalanceKafkaResponse;
import shop.shportfolio.trading.application.exception.UserBalanceNotFoundException;
import shop.shportfolio.trading.application.ports.input.kafka.UserBalanceAppliedListener;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.AssetCode;
import shop.shportfolio.trading.domain.valueobject.Money;
import shop.shportfolio.trading.domain.valueobject.UserBalanceId;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class UserBalanceAppliedListenerImpl implements UserBalanceAppliedListener {

    private final UserBalanceDomainService userBalanceDomainService;
    private final TradingUserBalanceRepositoryPort tradingUserBalanceRepositoryPort;

    @Autowired
    public UserBalanceAppliedListenerImpl(UserBalanceDomainService userBalanceDomainService,
                                          TradingUserBalanceRepositoryPort tradingUserBalanceRepositoryPort) {
        this.userBalanceDomainService = userBalanceDomainService;
        this.tradingUserBalanceRepositoryPort = tradingUserBalanceRepositoryPort;
    }

    @Override
    public void receiveUserBalance(UserBalanceKafkaResponse response) {
        Optional<UserBalance> balance = tradingUserBalanceRepositoryPort
                .findUserBalanceByUserId(response.getUserId());

        if (balance.isEmpty() && response.getAssetCode().equals(AssetCode.KRW.name())) {
            UserBalance userBalance = userBalanceDomainService.createUserBalance(new UserBalanceId(UUID.randomUUID()),
                    new UserId(response.getUserId()), AssetCode.fromString(response.getAssetCode()),
                    new Money(response.getAmount()));
            UserBalance saved = tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
            log.info("[UserBalance Created] userId={}, asset={}, amount={}, available={}",
                    saved.getUserId().getValue(),
                    saved.getAssetCode().name(),
                    response.getAmount(),
                    saved.getAvailableMoney().getValue()
            );

            return;
        }
        UserBalance userBalance = balance.orElseThrow(() ->
                new UserBalanceNotFoundException("UserBalance must exist at this point"));
        switch (response.getTransactionType()) {

            case DEPOSIT -> {
                if (!response.getAssetCode().equals(AssetCode.KRW.name())) {
                    throw new UnsupportedOperationException("AssetCode is not supported yet.");
                }
                userBalanceDomainService.depositMoney(userBalance, Money.of(response.getAmount()));
            }
            case WITHDRAWAL -> {
                if (!response.getAssetCode().equals(AssetCode.KRW.name())) {
                    throw new UnsupportedOperationException("AssetCode is not supported yet.");
                }
                userBalanceDomainService.withdrawMoney(userBalance, Money.of(response.getAmount()));
            }
            default -> {
                log.error("Unknown transaction type {}", response.getTransactionType());
                log.warn("only can be DEPOSIT or WITHDRAWAL");
                return;
            }
        }
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
    }

}
