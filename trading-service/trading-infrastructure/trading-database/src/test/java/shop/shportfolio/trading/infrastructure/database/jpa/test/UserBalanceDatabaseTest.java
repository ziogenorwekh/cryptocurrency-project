package shop.shportfolio.trading.infrastructure.database.jpa.test;

import com.querydsl.jpa.JPQLOps;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.LockStatus;
import shop.shportfolio.trading.domain.valueobject.UserBalanceId;
import shop.shportfolio.trading.infrastructure.database.jpa.adapter.UserBalanceRepositoryAdapter;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingUserBalanceDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.CryptoBalanceJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.UserBalanceJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.test.config.TestConfig;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserBalanceDatabaseTest {

    @Autowired
    private UserBalanceJpaRepository userBalanceJpaRepository;

    private UserBalanceRepositoryAdapter adapter;

    private TradingUserBalanceDataAccessMapper mapper;

    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private CryptoBalanceJpaRepository cryptoBalanceJpaRepository;

    private UserBalanceId userBalanceId = new UserBalanceId(UUID.randomUUID());
    private UserId userId = new UserId(UUID.randomUUID());
    private Money money = Money.of(BigDecimal.valueOf(1_000_000));
    private AssetCode assetCode = AssetCode.KRW;
    private List<LockBalance> lockBalances = new ArrayList<>();
    @BeforeEach
    public void setUp() {
        mapper = new TradingUserBalanceDataAccessMapper();
        adapter = new UserBalanceRepositoryAdapter(userBalanceJpaRepository,
                cryptoBalanceJpaRepository, mapper, queryFactory);
        lockBalances.add(LockBalance.builder()
                .userId(userId)
                .orderId(new OrderId(UUID.randomUUID().toString()))
                .lockedAmount(Money.of(BigDecimal.valueOf(900_000)))
                .lockedAt(CreatedAt.now())
                .lockStatus(LockStatus.LOCKED)
                .build()
        );
    }

    @Test
    @DisplayName("유저 밸런스 저장 테스트")
    public void saveUserBalanceTest() {
        // given
        UserBalance userBalance = UserBalance.builder()
                .userBalanceId(userBalanceId)
                .availableMoney(money)
                .lockBalances(lockBalances)
                .assetCode(assetCode)
                .userId(userId)
                .build();
        // when
        UserBalance saved = adapter.saveUserBalance(userBalance);
        // then
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(saved, userBalance);
        Assertions.assertEquals(1, saved.getLockBalances().size());
        Assertions.assertEquals(saved.getLockBalances().get(0).getUserId(), userBalance.getUserId());
        Assertions.assertEquals(saved.getAvailableMoney(), userBalance.getAvailableMoney());
        // when
        Optional<UserBalance> balance = adapter.findUserBalanceByUserId(userId.getValue());
        Assertions.assertTrue(balance.isPresent());
        Assertions.assertEquals(saved, balance.get());
        Assertions.assertEquals(1, saved.getLockBalances().size());
        Assertions.assertEquals(saved.getLockBalances().get(0).getUserId(), userBalance.getUserId());
    }
}
