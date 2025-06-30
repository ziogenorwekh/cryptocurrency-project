package shop.shportfolio.trading.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackResponse;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.OrderBook;

@Service
@Validated
public class TradingApplicationServiceImpl implements TradingApplicationService {

    private final TradingCreateOrderUseCase  createOrderUseCase;
    private final MarketOrderExecutionUseCase marketOrderExecutionUseCase;
    private final TradingTrackQueryUseCase tradingTrackQueryUseCase;
    private final TradingDataMapper tradingDataMapper;
    private final LimitOrderExecutionUseCase limitOrderExecutionUseCase;
    @Autowired
    public TradingApplicationServiceImpl(TradingCreateOrderUseCase createOrderUseCase,
                                         MarketOrderExecutionUseCase marketOrderExecutionUseCase,
                                         TradingTrackQueryUseCase tradingTrackQueryUseCase,
                                         TradingDataMapper tradingDataMapper,
                                         LimitOrderExecutionUseCase limitOrderExecutionUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.marketOrderExecutionUseCase = marketOrderExecutionUseCase;
        this.tradingTrackQueryUseCase = tradingTrackQueryUseCase;
        this.tradingDataMapper = tradingDataMapper;
        this.limitOrderExecutionUseCase = limitOrderExecutionUseCase;
    }

    @Override
    public CreateLimitOrderResponse createLimitOrder(CreateLimitOrderCommand createLimitOrderCommand) {
        LimitOrder limitOrder = createOrderUseCase.createLimitOrder(createLimitOrderCommand);
        limitOrderExecutionUseCase.executeLimitOrder(limitOrder);
        return tradingDataMapper.limitOrderToCreateLimitOrderResponse(limitOrder);
    }

    @Override
    public void createMarketOrder(CreateMarketOrderCommand createMarketOrderCommand) {
        MarketOrder marketOrder = createOrderUseCase.createMarketOrder(createMarketOrderCommand);
        marketOrderExecutionUseCase.executeMarketOrder(marketOrder);
    }

    @Override
    public OrderBookTrackResponse findOrderBook(OrderBookTrackQuery orderBookTrackQuery) {
        OrderBook orderBook = tradingTrackQueryUseCase.findOrderBook(orderBookTrackQuery);
        return tradingDataMapper.orderBookToOrderBookTrackResponse(orderBook);
    }
}
