package shop.shportfolio.trading.application.handler.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.dto.OrderBookBidsDto;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
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

    public MarketItem findMarketItemById(MarketId marketId) {
        return tradingRepositoryAdapter.findMarketItemById(marketId.getValue()).orElseThrow(() ->
        {
            log.info("marketId:{} not found", marketId.getValue());
            throw new MarketItemNotFoundException(String.format("MarketItem with id %s not found", marketId.getValue()));
        }
        );
    }

    /**
     * 체결은 되는데 매수 사이즈가 변하지 않음 그래서 2번 호출해야 하는게 1번만 호출되는 것.
     * 수정요망
     *
     * @param orderBook
     * @param marketOrder
     * @return
     */
    public List<TradingRecordedEvent> execAsksMarketOrder(OrderBook orderBook,
                                                          MarketOrder marketOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();
        log.info("Start executing ASKS MarketOrder. OrderId={}, RemainingQty={}",
                marketOrder.getId().getValue(), marketOrder.getRemainingQuantity().getValue());

        NavigableMap<TickPrice, PriceLevel> sellPriceLevels = orderBook.getSellPriceLevels();

        while (!sellPriceLevels.isEmpty() && marketOrder.isOpen()) {
            Map.Entry<TickPrice, PriceLevel> entry = sellPriceLevels.firstEntry();
            TickPrice tickPrice = entry.getKey();
            PriceLevel priceLevel = entry.getValue();
            while (marketOrder.isOpen() && !priceLevel.isEmpty()) {
                Order remainingOrder = priceLevel.peekOrder();
                Quantity remainingQuantity = remainingOrder.getRemainingQuantity();
                Quantity quantity = tradingDomainService.applyTrade(marketOrder, remainingQuantity);
                if (quantity.isZero()) {

                }
            }
        }
        return trades;
    }


    public List<TradingRecordedEvent> execBidMarketOrder(OrderBook orderBook,
                                                         MarketOrder marketOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        return trades;
    }
}
