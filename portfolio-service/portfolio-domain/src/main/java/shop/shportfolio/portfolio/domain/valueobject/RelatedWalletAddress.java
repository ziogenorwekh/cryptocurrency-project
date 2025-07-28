package shop.shportfolio.portfolio.domain.valueobject;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.ValueObject;

@Getter
public class RelatedWalletAddress extends ValueObject<String> {

    private final WalletType walletType;

    public RelatedWalletAddress(String value, WalletType walletType) {
        super(value);
        if (walletType == null) {
            throw new IllegalArgumentException("WalletType cannot be null");
        }
        this.walletType = walletType;
    }

    public static RelatedWalletAddress empty() {
        return new RelatedWalletAddress("", WalletType.UNKNOWN);
    }

    public Boolean isEmpty() {
        return walletType == WalletType.UNKNOWN;
    }
}
