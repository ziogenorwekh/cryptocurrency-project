package shop.shportfolio.user.database.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import shop.shportfolio.user.database.jpa.adapter.UserTrHistoryRepositoryPortImpl;
import shop.shportfolio.user.domain.entity.TransactionHistory;
import shop.shportfolio.common.domain.valueobject.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = {JpaTestConfiguration.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2,
        replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserTrDatabaseJpaTest {

    @Autowired
    private UserTrHistoryRepositoryPortImpl userTrHistoryRepositoryAdapter;

    private final UUID userId = UUID.randomUUID();
    private final UUID transactionId = UUID.randomUUID();
    private final String marketId = "BTC-KRW";

    @Test
    @DisplayName("거래 내역 저장 및 조회 테스트")
    public void saveAndFindTransactionHistory() {
        // given
        TransactionHistory transactionHistory = TransactionHistory.builder()
                .transactionHistoryId(transactionId)
                .userId(userId)
                .marketId(marketId)
                .transactionType(TransactionType.TRADE_BUY)
                .orderId("Anonymous")
                .orderPrice(BigDecimal.valueOf(10000000))
                .transactionTime(LocalDateTime.now())
                .quantity(BigDecimal.valueOf(1))
                .build();

        // when
        TransactionHistory saved = userTrHistoryRepositoryAdapter.save(transactionHistory);
        Optional<TransactionHistory> found = userTrHistoryRepositoryAdapter
                .findUserTransactionHistoryByUserIdAndHistoryId(userId, transactionId);
        List<TransactionHistory> listByUser = userTrHistoryRepositoryAdapter
                .findUserTransactionHistoryByUserId(userId);

        // then
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(transactionId, saved.getId().getValue());
        Assertions.assertEquals(userId, saved.getUserId().getValue());
        Assertions.assertEquals(marketId, saved.getMarketId().getValue());
        Assertions.assertEquals(TransactionType.TRADE_BUY, saved.getTransactionType());
        Assertions.assertEquals(BigDecimal.valueOf(10000000), saved.getOrderPrice().getValue());
        Assertions.assertNotNull(saved.getTransactionTime());

        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(saved, found.get());

        Assertions.assertFalse(listByUser.isEmpty());
        Assertions.assertTrue(listByUser.contains(saved));
    }
}
