package shop.shportfolio.portfolio.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.command.track.CryptoBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.track.CurrencyBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.track.PortfolioTrackQuery;
import shop.shportfolio.portfolio.application.command.track.TotalBalanceTrackQuery;
import shop.shportfolio.portfolio.application.dto.TotalBalanceContext;
import shop.shportfolio.portfolio.application.exception.BalanceNotFoundException;
import shop.shportfolio.portfolio.application.exception.InvalidRequestException;
import shop.shportfolio.portfolio.application.exception.PortfolioNotFoundException;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;

import java.util.List;

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
                .orElseThrow(() -> new BalanceNotFoundException(String.format("You do not hold any" +
                        " cryptocurrency in the %s market.", query.getMarketId())));
    }

    public CurrencyBalance findCurrencyBalanceByPortfolioId(CurrencyBalanceTrackQuery query) {
        return portfolioRepository.findCurrencyBalanceByPortfolioId(query.getPortfolioId(),query.getUserId())
                .orElseThrow(()->new BalanceNotFoundException(String.format("portfolioId: %d is not found currency" +
                        " balance. ", query.getPortfolioId())));
    }

    public Portfolio findPortfolioByUserId(PortfolioTrackQuery query) {
        return portfolioRepository.findPortfolioByUserId(query.getUserId())
                .orElseThrow(()-> new PortfolioNotFoundException(String.format("userId: {} is not found.",
                        query.getUserId())));
    }

    public TotalBalanceContext findBalances(TotalBalanceTrackQuery query) {
        List<CryptoBalance> cryptoBalances = portfolioRepository.findCryptoBalancesByPortfolioId(query.getPortfolioId());
        CurrencyBalance currencyBalance = this.findCurrencyBalanceByPortfolioId(
                new CurrencyBalanceTrackQuery(query.getPortfolioId()));
        return new TotalBalanceContext(cryptoBalances, currencyBalance);
    }
}
