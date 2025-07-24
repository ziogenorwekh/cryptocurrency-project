package shop.shportfolio.trading.domain.valueobject;

public enum AssetCode {
    KRW;


    public static AssetCode fromString(String assetCode) {
        return AssetCode.valueOf(assetCode.toUpperCase());
    }
}
