package shop.shportfolio.trading.domain.valueobject;

//[OPEN]
//        ├── 체결 전량 완료 → [FILLED]
//        ├── 사용자 취소     → [CANCELLED]
//        └── 일부 체결 중     → 여전히 [OPEN]
public enum OrderStatus {
    OPEN, FILLED, CANCELED;


    public boolean isFinal() {
        return this == FILLED || this == CANCELED;
    }
}
