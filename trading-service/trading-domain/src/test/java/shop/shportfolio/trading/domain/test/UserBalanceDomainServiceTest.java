package shop.shportfolio.trading.domain.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.trading.domain.valueobject.UserBalanceId;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserBalanceDomainServiceTest {

    private final UserId userId = new UserId(UUID.randomUUID());
    private final AssetCode assetCode = AssetCode.KRW;
    private final UserBalanceId balanceId = new UserBalanceId(UUID.randomUUID());
    private final Money initialAvailable = Money.of(new BigDecimal("1000.00"));

    @Test
    @DisplayName("잔고 생성")
    void create_user_balance() {
        UserBalance userBalance = UserBalance.createUserBalance(
                balanceId,
                userId,
                assetCode,
                initialAvailable,
                null
        );

        assertThat(userBalance.getAvailableMoney().getValue()).isEqualByComparingTo("1000.00");
        assertThat(userBalance.getLockBalances()).isEmpty();
    }

    @Test
    @DisplayName("가용 잔고 잠금")
    void lock_money_success() {
        UserBalance userBalance = UserBalance.createUserBalance(
                balanceId, userId, assetCode, initialAvailable, null);

        userBalance.lockMoney(new OrderId(UUID.randomUUID().toString()), Money.of(new BigDecimal("200.00")));

        assertThat(userBalance.getAvailableMoney().getValue()).isEqualByComparingTo("800.00");
        assertThat(userBalance.getLockBalances()).hasSize(1);
    }

    @Test
    @DisplayName("가용 잔고 잠금 실패 - 잔액 부족")
    void lock_money_insufficient() {
        UserBalance userBalance = UserBalance.createUserBalance(
                balanceId, userId, assetCode, initialAvailable, null);

        assertThatThrownBy(() ->
                userBalance.lockMoney(new OrderId(UUID.randomUUID().toString()), Money.of(new BigDecimal("2000.00")))
        ).isInstanceOf(TradingDomainException.class)
                .hasMessageContaining("Insufficient available balance");
    }

    @Test
    @DisplayName("잠금 해제")
    void unlock_money_success() {
        UserBalance userBalance = UserBalance.createUserBalance(
                balanceId, userId, assetCode, initialAvailable, null);

        OrderId orderId = new OrderId(UUID.randomUUID().toString());
        userBalance.lockMoney(orderId, Money.of(new BigDecimal("200.00")));

        userBalance.unlockMoney(orderId, Money.of(new BigDecimal("200.00")));

        assertThat(userBalance.getAvailableMoney().getValue()).isEqualByComparingTo("1000.00");
        assertThat(userBalance.getLockBalances()).isEmpty();
    }

    @Test
    @DisplayName("거래 체결 후 잔고 차감")
    void deduct_balance_for_trade_success() {
        UserBalance userBalance = UserBalance.createUserBalance(
                balanceId, userId, assetCode, initialAvailable, null);

        OrderId orderId = new OrderId(UUID.randomUUID().toString());
        userBalance.lockMoney(orderId, Money.of(new BigDecimal("300.00")));

        userBalance.deductBalanceForTrade(orderId, Money.of(new BigDecimal("300.00")));

        assertThat(userBalance.getLockBalances()).isEmpty();
        assertThat(userBalance.getAvailableMoney().getValue()).isEqualByComparingTo("700.00");
    }

    @Test
    @DisplayName("입금")
    void deposit_success() {
        UserBalance userBalance = UserBalance.createUserBalance(
                balanceId, userId, assetCode, initialAvailable, null);

        userBalance.deposit(Money.of(new BigDecimal("500.00")));

        assertThat(userBalance.getAvailableMoney().getValue()).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("출금")
    void withdraw_success() {
        UserBalance userBalance = UserBalance.createUserBalance(
                balanceId, userId, assetCode, initialAvailable, null);

        userBalance.withdraw(Money.of(new BigDecimal("500.00")));

        assertThat(userBalance.getAvailableMoney().getValue()).isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("출금 실패 - 잔액 부족")
    void withdraw_insufficient() {
        UserBalance userBalance = UserBalance.createUserBalance(
                balanceId, userId, assetCode, initialAvailable, null);

        assertThatThrownBy(() ->
                userBalance.withdraw(Money.of(new BigDecimal("1500.00")))
        ).isInstanceOf(TradingDomainException.class)
                .hasMessageContaining("Insufficient available balance");
    }
}
