package shop.shportfolio.portfolio.domain.valueobject;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.ValueObject;

@Getter
public class RelatedWalletAddress extends ValueObject<String> {

    private final WalletType walletType;
    private final String bankName;

    public RelatedWalletAddress(String value, String bankName, WalletType walletType) {
        super(value);
        if (walletType == null) {
            throw new IllegalArgumentException("WalletType cannot be null");
        }
        if (walletType == WalletType.BANK_ACCOUNT && (bankName == null || bankName.isBlank())) {
            throw new IllegalArgumentException("Bank name must be provided for BANK_ACCOUNT wallet type");
        }
        if (walletType != WalletType.BANK_ACCOUNT && bankName != null && !bankName.isBlank()) {
            throw new IllegalArgumentException("Bank name should be empty unless wallet type is BANK_ACCOUNT");
        }
        this.walletType = walletType;
        this.bankName = bankName == null ? "" : bankName;
    }
    public static RelatedWalletAddress empty() {
        return new RelatedWalletAddress("", "",WalletType.UNKNOWN);
    }

    public Boolean isEmpty() {
        return walletType == WalletType.UNKNOWN;
    }
}
