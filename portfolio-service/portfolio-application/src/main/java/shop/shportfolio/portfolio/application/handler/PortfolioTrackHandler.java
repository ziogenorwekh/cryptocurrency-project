package shop.shportfolio.portfolio.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.command.track.CryptoBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.track.CurrencyBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.track.TotalAssetValueTrackQuery;
import shop.shportfolio.portfolio.application.exception.BalanceNotFoundException;
import shop.shportfolio.portfolio.application.exception.InvalidRequestException;
import shop.shportfolio.portfolio.application.exception.PortfolioNotFoundException;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;

@Slf4j
@Component
public class PortfolioTrackHandler {


    private final PortfolioRepositoryPort portfolioRepository;

    @Autowired
    public PortfolioTrackHandler(PortfolioRepositoryPort portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public CryptoBalance findCryptoBalanceByPortfolioIdAndMarketId(CryptoBalanceTrackQuery query) {
        if (query.getMarketId().equals("KRW")) {
            throw new InvalidRequestException("Market Id is KRW. you can only access crypto Market Id.");
        }
        return portfolioRepository.findCryptoBalanceByPortfolioIdAndMarketId(query.getPortfolioId(), query.getMarketId())
                .orElseThrow(() -> new BalanceNotFoundException(String.format("marketId: {} , userId: {} is not found. ",
                        query.getMarketId(), query.getPortfolioId())));
    }

    public CurrencyBalance findCurrencyBalanceByUserId(CurrencyBalanceTrackQuery query) {
    }


    public Portfolio findPortfolioByPortfolioIdAndUserId(TotalAssetValueTrackQuery query) {
        return portfolioRepository.findPortfolioByPortfolioIdAndUserId(query.getPortfolioId(), query.getUserId())
                .orElseThrow(()-> new PortfolioNotFoundException(String.format("userId: {}, portfolioId: {} is not found.",
                        query.getUserId(),query.getPortfolioId())));
    }
}
