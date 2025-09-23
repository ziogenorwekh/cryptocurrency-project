package shop.shportfolio.marketdata.insight.application.test.factory;


import shop.shportfolio.marketdata.insight.application.dto.marketdata.MarketItemBithumbDto;

import java.util.List;

public class MarketItemTestFactory {

    public static List<MarketItemBithumbDto> createMockMarketList() {
        return List.of(
                new MarketItemBithumbDto("KRW-BTC", "비트코인", "Bitcoin"),
                new MarketItemBithumbDto("KRW-ETH", "이더리움", "Ethereum"),
                new MarketItemBithumbDto("KRW-XRP", "엑스알피 [리플]", "XRP"),
                new MarketItemBithumbDto("KRW-ADA", "에이다", "Cardano"),
                new MarketItemBithumbDto("KRW-DOGE", "도지코인", "Dogecoin"),
                new MarketItemBithumbDto("KRW-BCH", "비트코인 캐시", "Bitcoin Cash"),
//                new MarketItemBithumbDto("KRW-TRX", "트론", "TRON"),
                new MarketItemBithumbDto("KRW-XLM", "스텔라루멘", "Stellar Lumens"),
                new MarketItemBithumbDto("KRW-LINK", "체인링크", "ChainLink"),
//                new MarketItemBithumbDto("KRW-DOT", "폴카닷", "Polkadot"),
//                new MarketItemBithumbDto("KRW-SAND", "샌드박스", "The Sandbox"),
                new MarketItemBithumbDto("KRW-SOL", "솔라나", "Solana")
//                new MarketItemBithumbDto("KRW-ATOM", "코스모스", "Cosmos"),
//                new MarketItemBithumbDto("KRW-ALGO", "알고랜드", "Algorand")
        );
    }
}
