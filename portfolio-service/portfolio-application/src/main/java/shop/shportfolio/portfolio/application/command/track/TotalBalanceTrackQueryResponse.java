package shop.shportfolio.portfolio.application.command.track;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TotalBalanceTrackQueryResponse {

    private final CurrencyBalanceTrackQueryResponse  currencyBalanceTrackQueryResponse;
    private final List<CryptoBalanceTrackQueryResponse>  cryptoBalanceTrackQueryResponses;

    public TotalBalanceTrackQueryResponse(CurrencyBalanceTrackQueryResponse currencyBalanceTrackQueryResponse,
                                          List<CryptoBalanceTrackQueryResponse> cryptoBalanceTrackQueryResponses) {
        this.currencyBalanceTrackQueryResponse = currencyBalanceTrackQueryResponse;
        this.cryptoBalanceTrackQueryResponses = new ArrayList<>(cryptoBalanceTrackQueryResponses);
    }
}
