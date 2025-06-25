package shop.shportfolio.trading.application.handler.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.dto.OrderBookBidsDto;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TradingCreateHandler {

    private final TradingRepositoryAdapter tradingRepositoryAdapter;
    private final TradingDomainService tradingDomainService;


    @Autowired
    public TradingCreateHandler(TradingRepositoryAdapter tradingRepositoryAdapter,
                                TradingDomainService tradingDomainService) {
        this.tradingRepositoryAdapter = tradingRepositoryAdapter;
        this.tradingDomainService = tradingDomainService;
    }

    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        LimitOrder limitOrder = tradingDomainService.createLimitOrder(new UserId(command.getUserId()), new MarketId(command.getMarketId()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()), new OrderPrice(command.getPrice())
                , OrderType.valueOf(command.getOrderType()));
        return tradingRepositoryAdapter.saveLimitOrder(limitOrder);
    }

    public MarketOrder createMarketOrder(CreateMarketOrderCommand command) {
        MarketOrder marketOrder = tradingDomainService.createMarketOrder(new UserId(command.getUserId()),
                new MarketId(command.getMarketId()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                OrderType.valueOf(command.getOrderType()));
        return tradingRepositoryAdapter.saveMarketOrder(marketOrder);
    }

    /**
     * 체결은 되는데 매수 사이즈가 변하지 않음 그래서 2번 호출해야 하는게 1번만 호출되는 것.
     * 수정요망
     * @param orderBookAsksDto
     * @param marketOrder
     * @return
     */
    public List<TradingRecordedEvent> execAsksMarketOrder(List<OrderBookAsksDto> orderBookAsksDto,
                                                         MarketOrder marketOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();
        for (OrderBookAsksDto asks : orderBookAsksDto) {
            Quantity execQuantity = new Quantity(BigDecimal.valueOf(asks.getAskSize()));
            Boolean result = tradingDomainService.applyTrade(marketOrder, execQuantity);
            if (!result || marketOrder.isFilled()) {
                break;
            }
            trades.add(
                    tradingDomainService.createMarketTrade(new TradeId(UUID.randomUUID()), marketOrder.getUserId(),
                            marketOrder.getId(), new OrderPrice(BigDecimal.valueOf(asks.getAskPrice())),
                            execQuantity, new CreatedAt(LocalDateTime.now()),
                            TransactionType.fromString(marketOrder.getOrderSide().getValue())));
        }
        if (marketOrder.isFilled()) {
            tradingRepositoryAdapter.saveMarketOrder(marketOrder);
        }
        return trades;
    }


    public List<TradingRecordedEvent> execBidMarketOrder(List<OrderBookBidsDto> orderBookBidsDto,
                                                         MarketOrder marketOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();
        for (OrderBookBidsDto bids : orderBookBidsDto) {
            Quantity execQuantity = new Quantity(BigDecimal.valueOf(bids.getBidSize()));
            Boolean result = tradingDomainService.applyTrade(marketOrder, execQuantity);
            if (!result || marketOrder.isFilled()) {
                break;
            }
            System.out.println("매칭 시작 - order: " + marketOrder);
            System.out.println("호가 목록: " + orderBookBidsDto);
            trades.add(
                    tradingDomainService.createMarketTrade(new TradeId(UUID.randomUUID()), marketOrder.getUserId(),
                            marketOrder.getId(), new OrderPrice(BigDecimal.valueOf(bids.getBidPrice())),
                            execQuantity, new CreatedAt(LocalDateTime.now()),
                            TransactionType.fromString(marketOrder.getOrderSide().getValue())));
        }
        if (marketOrder.isFilled()) {
            tradingRepositoryAdapter.saveMarketOrder(marketOrder);
        }
        return trades;
    }
}
