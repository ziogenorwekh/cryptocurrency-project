package shop.shportfolio.trading.domain.valueobject;

public enum LockStatus {
    LOCKED,       // 락 활성화 (처음)
    PARTIALLY,    // 락 일부만 남음
    RELEASED      // 락 해제 (완전 체결)
}
