//package shop.shportfolio.trading.application.scheduler;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
//
//@Component
//public class ExpiredCouponCleanupScheduler {
//
//    private final TradingCouponRepositoryPort tradingCouponRepositoryPort;
//
//    @Autowired
//    public ExpiredCouponCleanupScheduler(TradingCouponRepositoryPort tradingCouponRepositoryPort) {
//        this.tradingCouponRepositoryPort = tradingCouponRepositoryPort;
//    }
//
//
//    @Scheduled(cron = "0 0 0 * * *")
//    public void run() {
//        tradingCouponRepositoryPort.deleteAllExpiredCoupons();
//    }
//}
