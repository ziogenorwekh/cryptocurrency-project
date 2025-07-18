package shop.shportfolio.trading.domain.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.AssetCode;
import shop.shportfolio.trading.domain.valueobject.Money;
import shop.shportfolio.trading.domain.valueobject.UserBalanceId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserBalanceDomainServiceTest {

    private UserBalance userBalance;

    @BeforeEach
    void setUp() {
        userBalance = UserBalance.createUserBalance(
                new UserBalanceId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                AssetCode.KRW,
                Money.of(BigDecimal.valueOf(100.0)),
                new ArrayList<>()
        );
    }

    @Test
    void deposit_shouldIncreaseAvailableBalance() {
        userBalance.deposit(Money.of(BigDecimal.valueOf(50.0)));
        assertEquals(BigDecimal.valueOf(150.0), userBalance.getAvailableMoney().getValue());
    }

    @Test
    void deposit_shouldThrowExceptionWhenNegative() {
        assertThrows(TradingDomainException.class,
                () -> userBalance.deposit(Money.of(BigDecimal.valueOf(-10.0))));
    }

    @Test
    void withdraw_shouldDecreaseAvailableBalance() {
        userBalance.withdraw(Money.of(BigDecimal.valueOf(30.0)));
        assertEquals(BigDecimal.valueOf(70.0), userBalance.getAvailableMoney().getValue());
    }

    @Test
    void withdraw_shouldThrowExceptionWhenInsufficientBalance() {
        assertThrows(TradingDomainException.class,
                () -> userBalance.withdraw(Money.of(BigDecimal.valueOf(200.0))));
    }

    @Test
    void withdraw_shouldThrowExceptionWhenNegative() {
        assertThrows(TradingDomainException.class,
                () -> userBalance.withdraw(Money.of(BigDecimal.valueOf(-5.0))));
    }

    @Test
    void lockMoney_shouldDecreaseAvailableAndIncreaseLocked() {
        userBalance.lockMoney(Money.of(BigDecimal.valueOf(40.0)));
        assertEquals(BigDecimal.valueOf(60.0), userBalance.getAvailableMoney().getValue());
        assertEquals(BigDecimal.valueOf(40.0), userBalance.getLockedMoney().getValue());
    }

    @Test
    void lockMoney_shouldThrowExceptionWhenInsufficientBalance() {
        assertThrows(TradingDomainException.class,
                () -> userBalance.lockMoney(Money.of(BigDecimal.valueOf(200.0))));
    }

    @Test
    void unlockMoney_shouldIncreaseAvailableAndDecreaseLocked() {
        userBalance.lockMoney(Money.of(BigDecimal.valueOf(30.0)));
        userBalance.unlockMoney(Money.of(BigDecimal.valueOf(10.0)));
        assertEquals(BigDecimal.valueOf(80.0), userBalance.getAvailableMoney().getValue());
        assertEquals(BigDecimal.valueOf(20.0), userBalance.getLockedMoney().getValue());
    }

    @Test
    void unlockMoney_shouldThrowExceptionWhenLockedInsufficient() {
        assertThrows(TradingDomainException.class,
                () -> userBalance.unlockMoney(Money.of(BigDecimal.valueOf(10.0))));
    }

    @Test
    void deductBalanceForTrade_shouldDecreaseAvailableBalance() {
        userBalance.deductBalanceForTrade(Money.of(BigDecimal.valueOf(50.0)));
        assertEquals(BigDecimal.valueOf(50.0), userBalance.getAvailableMoney().getValue());
    }

    @Test
    void deductBalanceForTrade_shouldThrowExceptionWhenInsufficientBalance() {
        assertThrows(TradingDomainException.class,
                () -> userBalance.deductBalanceForTrade(Money.of(BigDecimal.valueOf(200.0))));
    }
}
