package shop.shportfolio.trading.application.initializer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.scheduler.MarketHardCodingData;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;

@Slf4j
@Component
public class InitializeMarketItem {
    private final BithumbApiPort bithumbApiPort;
    private final TradingDtoMapper tradingDtoMapper;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;

    @Autowired
    public InitializeMarketItem(BithumbApiPort bithumbApiPort, TradingDtoMapper tradingDtoMapper,
                                TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort) {
        this.bithumbApiPort = bithumbApiPort;
        this.tradingDtoMapper = tradingDtoMapper;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
    }

    @PostConstruct
    public void init() {
        saveMarketCode();
    }

    private void saveMarketCode() {
        bithumbApiPort.findMarketItems().forEach(marketItemBithumbDto -> {
            MarketHardCodingData.marketMap.forEach((marketId, tickPrice) -> {
                if (marketId.equals(marketItemBithumbDto.getMarketId())) {
                    MarketItem entity = tradingDtoMapper.marketItemBithumbDtoToMarketItem(marketItemBithumbDto,
                            tickPrice);
                    tradingMarketDataRepositoryPort.saveMarketItem(entity);
                    log.info("MarketItem saved: {}", marketId);
                }
            });
        });
    }
}
