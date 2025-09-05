package shop.shportfolio.trading.application.validator;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.exception.OrderInValidatedException;
import shop.shportfolio.trading.application.policy.PriceLimitPolicy;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;

@Component
public class LimitOrderValidator implements OrderValidator<LimitOrder> {

    private final BithumbApiPort bithumbApiPort;
    private final PriceLimitPolicy priceLimitPolicy;

    public LimitOrderValidator(BithumbApiPort bithumbApiPort,
            PriceLimitPolicy priceLimitPolicy) {
        this.bithumbApiPort = bithumbApiPort;
        this.priceLimitPolicy = priceLimitPolicy;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.LIMIT.equals(order.getOrderType());
    }

    @Override
    public void validateBuyOrder(LimitOrder order) {
        OrderBookBithumbDto book = bithumbApiPort.findOrderBookByMarketId(order.getMarketId().getValue());

        // 총 매도량 체크
        if (order.getQuantity().getValue().compareTo(BigDecimal.valueOf(book.getTotalAskSize())) > 0) {
            throw new OrderInValidatedException("Buy order quantity exceeds available sell liquidity.");
        }

        // 최저 Ask 가격 체크
        if (book.getAsks() != null && !book.getAsks().isEmpty()) {
            double lowestAsk = book.getAsks().get(0).getAskPrice();
            if (priceLimitPolicy.isOverTenPercentHigher(order.getOrderPrice(), BigDecimal.valueOf(lowestAsk))) {
                throw new OrderInValidatedException("Limit buy order price is more than 10% above best ask.");
            }
        }
    }

    @Override
    public void validateSellOrder(LimitOrder order) {
        OrderBookBithumbDto book = bithumbApiPort.findOrderBookByMarketId(order.getMarketId().getValue());

        // 총 매수량 체크
        if (order.getQuantity().getValue().compareTo(BigDecimal.valueOf(book.getTotalBidSize())) > 0) {
            throw new OrderInValidatedException("Sell order quantity exceeds available buy liquidity.");
        }

        // 최고 Bid 가격 체크
        if (book.getBids() != null && !book.getBids().isEmpty()) {
            double highestBid = book.getBids().get(0).getBidPrice();
            if (priceLimitPolicy.isOverTenPercentLower(order.getOrderPrice(), BigDecimal.valueOf(highestBid))) {
                throw new OrderInValidatedException("Limit sell order price is more than 10% below best bid.");
            }
        }
    }
}
