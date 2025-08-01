package shop.shportfolio.portfolio.api.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.shportfolio.portfolio.application.command.track.CryptoBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.track.CryptoBalanceTrackQueryResponse;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;

@Slf4j
@RestController
public class TrackPortfolioResources {

    private final PortfolioApplicationService portfolioApplicationService;

    @Autowired
    public TrackPortfolioResources(PortfolioApplicationService portfolioApplicationService) {
        this.portfolioApplicationService = portfolioApplicationService;
    }


    @RequestMapping(path = "/crypto/balance/{marketId}")
    public ResponseEntity<CryptoBalanceTrackQueryResponse> trackCryptoBalance(@PathVariable String marketId,
            @RequestBody CryptoBalanceTrackQuery cryptoBalanceTrackQuery) {
        cryptoBalanceTrackQuery.setMarketId(marketId);
        CryptoBalanceTrackQueryResponse response = portfolioApplicationService
                .trackCryptoBalance(cryptoBalanceTrackQuery);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
