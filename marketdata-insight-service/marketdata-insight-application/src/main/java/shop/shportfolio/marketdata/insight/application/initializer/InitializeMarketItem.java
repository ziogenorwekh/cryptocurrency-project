package shop.shportfolio.marketdata.insight.application.initializer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.mapper.MarketDataDtoMapper;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.MarketItemRepositoryPort;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;

@Slf4j
@Component
public class InitializeMarketItem {

    private final MarketItemRepositoryPort repository;
    private final BithumbApiPort bithumbApiPort;
    private final MarketDataDtoMapper mapper;
    @Autowired
    public InitializeMarketItem(MarketItemRepositoryPort repository,
                                BithumbApiPort bithumbApiPort, MarketDataDtoMapper mapper) {
        this.repository = repository;
        this.bithumbApiPort = bithumbApiPort;
        this.mapper = mapper;
    }

    @PostConstruct
    public void init() {
        saveMarketCode();
    }

    public void saveMarketCode() {
        bithumbApiPort.findMarketItems().forEach(marketItemBithumbDto -> {
            MarketHardCodingData.marketMap.forEach((marketId, tickPrice) -> {
                if (marketId.equals(marketItemBithumbDto.getMarketId())) {
                    MarketItem entity = mapper.marketItemBithumbDtoToMarketItem(marketItemBithumbDto
                    );
                    repository.saveMarketItem(entity);
                    log.info("MarketItem saved: {}", marketId);
                }
            });
        });
    }
}
