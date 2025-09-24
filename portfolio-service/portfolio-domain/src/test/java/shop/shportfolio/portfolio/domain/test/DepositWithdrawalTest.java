package shop.shportfolio.portfolio.domain.test;

import org.junit.jupiter.api.*;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.DepositWithdrawalDomainService;
import shop.shportfolio.portfolio.domain.DepositWithdrawalDomainServiceImpl;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;
import shop.shportfolio.portfolio.domain.valueobject.RelatedWalletAddress;
import shop.shportfolio.portfolio.domain.valueobject.TransactionId;
import shop.shportfolio.portfolio.domain.valueobject.WalletType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DepositWithdrawalTest {

    private DepositWithdrawalDomainService depositWithdrawalDomainService;

    private static final UserId testUserId = new UserId(UUID.randomUUID());
    private static final TransactionId testTransactionId = new TransactionId(UUID.randomUUID());
    private static final Money testAmount = new Money(BigDecimal.valueOf(1000));
    private static final TransactionType testTransactionType = TransactionType.DEPOSIT;
    private static final TransactionTime testTransactionTime = new TransactionTime(LocalDateTime.now(ZoneOffset.UTC));
    private static final CreatedAt testCreatedAt = new CreatedAt(LocalDateTime.now(ZoneOffset.UTC));
    private static final RelatedWalletAddress testWalletAddress = new RelatedWalletAddress("123-123-123",
            "국민은행",WalletType.BANK_ACCOUNT);
    private static final UpdatedAt testUpdatedAt = new UpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

    @BeforeEach
    public void setup() {
        depositWithdrawalDomainService = new DepositWithdrawalDomainServiceImpl();
    }


    @Test
    @DisplayName("markFailed(가상 계좌)시 PENDING이 아니면 예외 발생 테스트")
    public void markFailed_WhenNotPending_ShouldThrowException() {

        PortfolioDomainException ex = Assertions.assertThrows(PortfolioDomainException.class, () -> {
            depositWithdrawalDomainService.createDeposit(
                testTransactionId, testUserId, testAmount, testTransactionType,
                testTransactionTime, TransactionStatus.FAILED,
                testWalletAddress, testCreatedAt, testUpdatedAt);
        });

        Assertions.assertEquals("TransactionStatus must be PENDING at creation", ex.getMessage());
    }

}
