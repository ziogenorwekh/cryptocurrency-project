package shop.shportfolio.portfolio.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.TransactionStatus;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.portfolio.domain.valueobject.WalletType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DEPOSIT_WITHDRAWAL_ENTITY")
public class DepositWithdrawalEntity {

    @Id
    @Column(name = "TRANSACTION_ID", unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID transactionId;

    @Column(name = "USER_ID", unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "AMOUNT", precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE")
    private TransactionType transactionType;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "TRANSACTION_TIME")
    private LocalDateTime transactionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_STATUS")
    private TransactionStatus transactionStatus;

    @Column(name = "RELATED_WALLET_ADDRESS")
    private String relatedWalletAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "WALLET_TYPE")
    private WalletType walletType;

    @Column(name = "BANK_NAME")
    private String bankName;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
