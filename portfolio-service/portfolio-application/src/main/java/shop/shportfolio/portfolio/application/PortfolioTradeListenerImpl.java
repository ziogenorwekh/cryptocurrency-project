package shop.shportfolio.portfolio.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.application.command.track.PortfolioTrackQuery;
import shop.shportfolio.portfolio.application.dto.TradeKafkaResponse;
import shop.shportfolio.portfolio.application.handler.AssetChangeLogHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioTrackHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioUpdateHandler;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioTradeListener;
import shop.shportfolio.portfolio.application.port.output.kafka.CryptoAmountPublisher;
import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.entity.view.CryptoView;
import shop.shportfolio.portfolio.domain.event.CryptoUpdatedEvent;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
public class PortfolioTradeListenerImpl implements PortfolioTradeListener {


    private final AssetChangeLogHandler assetChangeLogHandler;
    private final PortfolioUpdateHandler portfolioUpdateHandler;
    private final PortfolioTrackHandler portfolioTrackHandler;
    private final CryptoAmountPublisher cryptoAmountPublisher;
    @Autowired
    public PortfolioTradeListenerImpl(AssetChangeLogHandler assetChangeLogHandler,
                                      PortfolioUpdateHandler portfolioUpdateHandler,
                                      PortfolioTrackHandler portfolioTrackHandler,
                                      CryptoAmountPublisher cryptoAmountPublisher) {
        this.assetChangeLogHandler = assetChangeLogHandler;
        this.portfolioUpdateHandler = portfolioUpdateHandler;
        this.portfolioTrackHandler = portfolioTrackHandler;
        this.cryptoAmountPublisher = cryptoAmountPublisher;
    }

    @Override
    @Transactional
    public void handleTrade(TradeKafkaResponse response) {
        log.info("trade received {}", response);
        updateCryptoBalance(response);
        createAssetChangeLog(response);
    }

    private void updateCryptoBalance(TradeKafkaResponse response) {
        CryptoBalance cryptoBalance = portfolioUpdateHandler.updateCryptoBalance(response);
        log.info("updated CryptoBalance: {}", cryptoBalance);
        CryptoUpdatedEvent cryptoUpdatedEvent = new CryptoUpdatedEvent(CryptoView.toCryptoView(cryptoBalance,
                new UserId(UUID.fromString(response.getUserId()))),
                MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
        cryptoAmountPublisher.publish(cryptoUpdatedEvent);
    }

    private void createAssetChangeLog(TradeKafkaResponse response) {
        Portfolio portfolio = portfolioTrackHandler
                .findPortfolioByUserId(new PortfolioTrackQuery(UUID.fromString(response.getUserId())));
        AssetChangeLog assetChangeLog = assetChangeLogHandler.saveTrade(response, portfolio);
        log.info("saved AssetChangeLog: {}", assetChangeLog);
    }
}
