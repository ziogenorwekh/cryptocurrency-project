package shop.shportfolio.portfolio.application.dto;

import lombok.Getter;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TotalBalanceContext {

    private final List<CryptoBalance> cryptoBalances;
    private final CurrencyBalance currencyBalance;

    public TotalBalanceContext(List<CryptoBalance> cryptoBalances, CurrencyBalance currencyBalance) {
        this.cryptoBalances = cryptoBalances;
        this.currencyBalance = currencyBalance;
    }
}
