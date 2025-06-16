package application.test;


import application.tmpbean.TestUserTrHistoryMockBean;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.dto.TransactionHistoryDTO;
import shop.shportfolio.user.application.ports.input.TransactionHistoryApplicationService;
import shop.shportfolio.user.application.command.track.TrackUserTrHistoryQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrHistoryListTrackQuery;
import shop.shportfolio.user.application.command.track.UserTrHistoryOneTrackQuery;
import shop.shportfolio.user.application.handler.UserTrHistoryCommandHandler;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.application.ports.output.repository.UserTrHistoryRepositoryAdapter;
import shop.shportfolio.user.domain.entity.TransactionHistory;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TestUserTrHistoryMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class TransactionHistoryApplicationServiceTest {

    @Autowired
    private TransactionHistoryApplicationService transactionHistoryApplicationService;

    @Autowired
    private UserTrHistoryCommandHandler userTrHistoryCommandHandler;

    @Autowired
    private UserTrHistoryRepositoryAdapter userTrHistoryRepositoryAdapter;

    @Autowired
    private UserRepositoryAdaptor userDataRepositoryAdaptor;

    private final UUID userId = UUID.randomUUID();
    private List<TransactionHistory> transactionHistoryList;
    private final UUID tr0 =  UUID.randomUUID();
    private final UUID tr1 =  UUID.randomUUID();
    private final UUID tr2 =  UUID.randomUUID();
    private final UUID tr3 =  UUID.randomUUID();
    private final TransactionHistory transactionHistory0 = new TransactionHistory(new TransactionHistoryId(tr0),
            new UserId(userId), new MarketId("KRW_BTC"),
                TransactionType.TRADE_BUY, new Amount(BigDecimal.valueOf(10000000)),
                new TransactionTime(LocalDateTime.now().minusMinutes(30)));
    @Autowired
    private UserRepositoryAdaptor userRepositoryAdaptor;

    private final String username = "김철수";
    private final String phoneNumber = "01012345678";
    private final String email = "test@example.com";
    private final String password = "testpwd";
    User testUser = User.createUser(new UserId(userId), new Email(email),
            new PhoneNumber(phoneNumber), new Username(username), new Password(password));

    @BeforeEach
    public void beforeEach() {
        TransactionHistory transactionHistory1 = new TransactionHistory(new TransactionHistoryId(tr1)
                ,new UserId(userId), new MarketId("KRW_ETC"),
                TransactionType.WITHDRAWAL, new Amount(BigDecimal.valueOf(7000000)),
                new TransactionTime(LocalDateTime.now().minusHours(2)));
        TransactionHistory transactionHistory2 = new TransactionHistory(new TransactionHistoryId(tr2)
                ,new UserId(userId), new MarketId("KRW_SDX"),
                TransactionType.DEPOSIT, new Amount(BigDecimal.valueOf(2000000)),
                new TransactionTime(LocalDateTime.now().minusMonths(4)));
        TransactionHistory transactionHistory3 = new TransactionHistory(new TransactionHistoryId(tr3)
                ,new UserId(userId), new MarketId("KRW_ADA"),
                TransactionType.TRADE_SELL, new Amount(BigDecimal.valueOf(100000)),
                new TransactionTime(LocalDateTime.now().minusDays(2)));
        transactionHistoryList = new ArrayList<>();

        transactionHistoryList.add(transactionHistory0);
        transactionHistoryList.add(transactionHistory1);
        transactionHistoryList.add(transactionHistory2);
        transactionHistoryList.add(transactionHistory3);
    }
    @AfterEach
    public void afterEach() {
        Mockito.reset(userTrHistoryRepositoryAdapter);
    }

    @Test
    @DisplayName("특정 유저의 거래내역 리스트 조회")
    public void transactionHistoryListUserTest() {
        // given 특정 유저아이디를 통해서 거래내역 리스트 조회
        UserTrHistoryListTrackQuery userTrHistoryListTrackQuery =
                new UserTrHistoryListTrackQuery(userId);
        Mockito.when(userTrHistoryRepositoryAdapter.findUserTransactionHistoryByUserId(userId))
                .thenReturn(transactionHistoryList);
        // when
        TrackUserTrHistoryQueryResponse trackUserTrHistoryQueryResponse =
                transactionHistoryApplicationService.findTransactionHistories(userTrHistoryListTrackQuery);
        // then
        Mockito.verify(userTrHistoryRepositoryAdapter, Mockito.times(1)).
                findUserTransactionHistoryByUserId(userId);
        Assertions.assertNotNull(trackUserTrHistoryQueryResponse);
        Assertions.assertEquals(4, trackUserTrHistoryQueryResponse.getTransactionHistoryList().size());
    }
    @Test
    @DisplayName("특정 유저의 거래내역 단건 조회")
    public void transactionHistoryAUserTrHistoryTest() {
        // given
        UserTrHistoryOneTrackQuery userTrHistoryOneTrackQuery = new UserTrHistoryOneTrackQuery(userId,
                tr0);
        Mockito.when(userTrHistoryRepositoryAdapter.findUserTransactionHistoryByUserIdAndHistoryId(userId,
                userTrHistoryOneTrackQuery.getTrHistoryId())).thenReturn(Optional.of(transactionHistory0));
        // when
        TrackUserTrHistoryQueryResponse oneTransactionHistory = transactionHistoryApplicationService
                .findOneTransactionHistory(userTrHistoryOneTrackQuery);
        // then

        Assertions.assertNotNull(oneTransactionHistory);
        Assertions.assertEquals(1, oneTransactionHistory.getTransactionHistoryList().size());
        Assertions.assertEquals("KRW_BTC", oneTransactionHistory.getTransactionHistoryList().
                get(0).getMarketId());
    }

    @Test
    @DisplayName("거래 내역 저장 테스트 (카프카)")
    public void saveTransactionHistoryTest() {
        // given
        Mockito.when(userRepositoryAdaptor.findByUserId(userId)).thenReturn(Optional.of(testUser));
        Mockito.when(userTrHistoryRepositoryAdapter.save(Mockito.any())).thenReturn(transactionHistory0);
        TransactionHistoryDTO transactionHistoryDTO = new TransactionHistoryDTO(
                transactionHistory0.getMarketId().getValue(), transactionHistory0.getTransactionType().name(),
                transactionHistory0.getAmount().getValue().toString(), transactionHistory0.getTransactionTime().getValue()
        );
        // when
        TransactionHistory saved = userTrHistoryCommandHandler.saveTransactionHistory(userId, transactionHistoryDTO);
        // then
        Mockito.verify(userTrHistoryRepositoryAdapter, Mockito.times(1))
                .save(Mockito.any(TransactionHistory.class));
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(saved, transactionHistory0);
    }
}
