package shop.shportfolio.trading.domain.valueobject;

//[OPEN]
//        ├─ 일부 체결 → [PARTIALLY_FILLED]
//        │   └─ 추가 체결 → [FILLED]
//        ├─ 전량 체결 → [FILLED]
//        └─ 사용자 취소 → [CANCELED]
public enum OrderStatus {
    OPEN,               // 미체결
    PARTIALLY_FILLED,   // 부분 체결
    FILLED,             // 전량 체결 완료
    CANCELED;           // 사용자 취소

    public boolean isFinal() {
        return this == FILLED || this == CANCELED;
    }

    public boolean isOpen() {
        return this == OPEN || this == PARTIALLY_FILLED;
    }
}