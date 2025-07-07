package shop.shportfolio.trading.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CouponAvroModel;
import shop.shportfolio.common.avro.TradeAvroModel;
import shop.shportfolio.common.avro.TransactionType;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.domain.entity.Trade;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class TradingMessageMapper {

    public CouponKafkaResponse couponResponseToCouponAvroModel(CouponAvroModel couponAvroModel) {
        return CouponKafkaResponse.builder()
                .couponId(new CouponId(UUID.fromString(couponAvroModel.getCouponId())))
                .owner(new UserId(UUID.fromString(couponAvroModel.getOwner())))
                .feeDiscount(new FeeDiscount((int)couponAvroModel.getFeeDiscount()))
                .issuedAt(new IssuedAt(couponAvroModel.getIssuedAt()))
                .expiryDate(new UsageExpiryDate(couponAvroModel.getExpiryDate()))
                .build();
    }

    public TradeAvroModel tradeToTradeAvroModel(Trade trade) {

        TransactionType avroTxType = switch (trade.getTransactionType()) {
            case DEPOSIT -> TransactionType.DEPOSIT;
            case WITHDRAWAL -> TransactionType.WITHDRAWAL;
            case TRADE_BUY -> TransactionType.TRADE_BUY;
            case TRADE_SELL -> TransactionType.TRADE_SELL;
        };

        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = trade.getCreatedAt().getValue().atZone(zoneId);
        return TradeAvroModel.newBuilder()
                .setTradeId(trade.getId().getValue().toString())
                .setUserId(trade.getUserId().getValue().toString())
                .setBuyOrderId(trade.getBuyOrderId().getValue())
                .setSellOrderId(trade.getSellOrderId().getValue())
                .setOrderPrice(trade.getOrderPrice().getValue().doubleValue())
                .setQuantity(trade.getQuantity().getValue().doubleValue())
                .setTransactionType(avroTxType)
                .setCreatedAt(zonedDateTime.toInstant())
                .build();
    }
}
