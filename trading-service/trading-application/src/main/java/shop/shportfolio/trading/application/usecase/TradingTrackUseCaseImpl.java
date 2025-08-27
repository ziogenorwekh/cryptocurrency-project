package shop.shportfolio.trading.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.orderbook.manager.OrderBookManager;
import shop.shportfolio.trading.application.handler.track.MarketDataTrackHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.input.TradingTrackUseCase;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.trade.Trade;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TradingTrackUseCaseImpl implements TradingTrackUseCase {

    private final TradingTrackHandler tradingTrackHandler;
    private final OrderBookManager orderBookManager;
    private final MarketDataTrackHandler marketDataTrackHandler;
    private final TradingDtoMapper tradingDtoMapper;
    private final BithumbApiPort bithumbApiPort;

    @Autowired
    public TradingTrackUseCaseImpl(TradingTrackHandler tradingTrackHandler,
                                   OrderBookManager orderBookManager,
                                   MarketDataTrackHandler marketDataTrackHandler,
                                   TradingDtoMapper tradingDtoMapper,
                                   BithumbApiPort bithumbApiPort) {
        this.tradingTrackHandler = tradingTrackHandler;
        this.orderBookManager = orderBookManager;
        this.marketDataTrackHandler = marketDataTrackHandler;
        this.tradingDtoMapper = tradingDtoMapper;
        this.bithumbApiPort = bithumbApiPort;
    }

    @Override
    public OrderBook findOrderBook(OrderBookTrackQuery orderBookTrackQuery) {
        MarketItem item = marketDataTrackHandler.findMarketItemByMarketId(orderBookTrackQuery.getMarketId());
        return orderBookManager.loadAdjustedOrderBook(orderBookTrackQuery.getMarketId()
        );
    }

    @Override
    public LimitOrder findLimitOrderByOrderId(LimitOrderTrackQuery limitOrderTrackQuery) {
        return tradingTrackHandler.findLimitOrderByOrderIdAndUserId(limitOrderTrackQuery.getOrderId()
                , limitOrderTrackQuery.getUserId());
    }

    @Override
    public ReservationOrder findReservationOrderByOrderIdAndUserId(ReservationOrderTrackQuery query) {
        return tradingTrackHandler
                .findReservationOrderByOrderIdAndUserId(query.getOrderId(), query.getUserId());
    }

    @Override
    public MarketTickerResponseDto findMarketTickerByMarket(TickerTrackQuery query) {
        // 1. API 호출
        MarketTickerResponseDto apiResult = bithumbApiPort.findTickerByMarketId(
                new MarketTickerRequestDto(query.getMarketId()));

        // 2. 내부 데이터 조회
        Optional<Trade> latestTrade = marketDataTrackHandler.findLatestTrade(query.getMarketId());

        // 3. 비교 후 DTO 변환
        if (latestTrade.isPresent()) {
            long tradeTimestamp = latestTrade.get().getCreatedAt().getValue()
                    .atZone(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli();

            if (tradeTimestamp > apiResult.getTimestamp()) {
                return tradingDtoMapper.tradeToMarketTickerResponseDto(latestTrade.get(), apiResult);
            }
        }
        return apiResult;
    }

    @Override
    public List<TradeTickResponseDto> findTradeTickByMarket(TradeTickTrackQuery query) {
        TradeTickRequestDto tradeTickRequestDto = tradingDtoMapper.toTradeTickRequestDto(
                query.getMarketId(), query.getTo(), query.getCount(), query.getCursor(), query.getDaysAgo());
        List<TradeTickResponseDto> bithumbApiPortTradeTicks = bithumbApiPort.findTradeTicks(tradeTickRequestDto);
        List<Trade> internalTrades = marketDataTrackHandler
                .findTradeTickByMarketId(query.getMarketId(), query.getTo(),
                query.getCount(), query.getDaysAgo());

        List<TradeTickResponseDto> parsing = internalTrades.stream()
                .map(tradingDtoMapper::tradeToTradeTickResponseDto)
                .toList();

        List<TradeTickResponseDto> merged = new ArrayList<>();
        merged.addAll(parsing);
        merged.addAll(bithumbApiPortTradeTicks);
        return merged;
    }


}
