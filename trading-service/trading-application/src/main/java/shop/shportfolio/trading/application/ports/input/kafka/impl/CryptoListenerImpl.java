package shop.shportfolio.trading.application.ports.input.kafka.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.trading.application.dto.crypto.CryptoKafkaResponse;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.input.kafka.CryptoListener;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.userbalance.CryptoBalance;

import java.util.Optional;

@Slf4j
@Component
public class CryptoListenerImpl implements CryptoListener {

    private final TradingDtoMapper tradingDtoMapper;
    private final TradingUserBalanceRepositoryPort tradingUserBalanceRepositoryPort;
    private final UserBalanceDomainService userBalanceDomainService;
    public CryptoListenerImpl(TradingDtoMapper tradingDtoMapper,
                              TradingUserBalanceRepositoryPort tradingUserBalanceRepositoryPort,
                              UserBalanceDomainService userBalanceDomainService) {
        this.tradingDtoMapper = tradingDtoMapper;
        this.tradingUserBalanceRepositoryPort = tradingUserBalanceRepositoryPort;
        this.userBalanceDomainService = userBalanceDomainService;
    }

    @Override
    @Transactional
    public void updateCrypto(CryptoKafkaResponse response) {
        log.info("Received update cryptoListenerImpl: {}", response);
        Optional<CryptoBalance> optional = tradingUserBalanceRepositoryPort.findCryptoBalanceByUserIdAndMarketId(response.getUserId(), response.getMarketId());
        CryptoBalance cryptoBalance;
        if (optional.isPresent()) {
            cryptoBalance = optional.get();
            userBalanceDomainService.updateQuantity(cryptoBalance, Quantity.of(response.getQuantity()));
            userBalanceDomainService.updatePurchasedAmount(cryptoBalance, Money.of(response.getPurchasePrice()));
        } else {
            cryptoBalance = tradingDtoMapper.toCryptoBalance(response);
        }
        tradingUserBalanceRepositoryPort.saveCryptoBalance(cryptoBalance);
    }
}
