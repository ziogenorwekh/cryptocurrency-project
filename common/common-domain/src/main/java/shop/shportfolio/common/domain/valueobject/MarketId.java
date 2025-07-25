package shop.shportfolio.common.domain.valueobject;

// KRT_BTC 빗썸의 API parameter market과 동일
public class MarketId extends BaseId<String>{
    public MarketId(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return super.getValue();
    }


    public Boolean isKRW() {
        return this.getValue().equals("KRW");
    }
}
