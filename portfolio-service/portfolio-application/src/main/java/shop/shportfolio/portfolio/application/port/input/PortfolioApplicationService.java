package shop.shportfolio.portfolio.application.port.input;

import jakarta.validation.Valid;
import shop.shportfolio.portfolio.application.command.create.*;
import shop.shportfolio.portfolio.application.command.track.*;

import java.util.List;

public interface PortfolioApplicationService {

    /**
     * 암호화폐 잔고 조회
     * @param cryptoBalanceTrackQuery
     * @return
     */
    CryptoBalanceTrackQueryResponse trackCryptoBalance(@Valid CryptoBalanceTrackQuery cryptoBalanceTrackQuery);

    /**
     * 법정화폐 잔고 조회
     * @param currencyBalanceTrackQuery
     * @return
     */
    CurrencyBalanceTrackQueryResponse trackCurrencyBalance(@Valid CurrencyBalanceTrackQuery currencyBalanceTrackQuery);

    /**
     * 포트폴리오 조회
     * @param portfolioTrackQuery
     * @return
     */
    PortfolioTrackQueryResponse trackPortfolio(@Valid PortfolioTrackQuery portfolioTrackQuery);

    /**
     * 전체 잔고 조회
     * @param totalBalanceTrackQuery
     * @return
     */
    TotalBalanceTrackQueryResponse trackTotalBalances(@Valid TotalBalanceTrackQuery totalBalanceTrackQuery);

    /**
     * 입금 처리
     * @param depositCreateCommand
     * @return
     */
    DepositCreatedResponse deposit(@Valid DepositCreateCommand depositCreateCommand);

    /**
     * 출금 처리
     * @param withdrawalCreateCommand
     * @return
     */

    WithdrawalCreatedResponse withdrawal(@Valid WithdrawalCreateCommand withdrawalCreateCommand);

    /**
     * 자산 변동 로그 조회
     * @param assetChangLogTrackQuery
     * @return
     */
    List<AssetChangLogTrackQueryResponse> trackAssetChangLog(@Valid AssetChangLogTrackQuery assetChangLogTrackQuery);
}
