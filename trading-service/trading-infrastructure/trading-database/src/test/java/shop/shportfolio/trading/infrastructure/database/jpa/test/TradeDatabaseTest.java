package shop.shportfolio.trading.infrastructure.database.jpa.test;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.common.domain.valueobject.TradeId;
import shop.shportfolio.trading.infrastructure.database.jpa.adapter.TradingTradeRecordRepositoryAdapter;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingTradeDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.TradeJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.test.config.TestConfig;

import java.math.BigDecimal;
import java.util.UUID;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class TradeDatabaseTest {

    private TradingTradeDataAccessMapper mapper;
    @Autowired
    private TradeJpaRepository tradeJpaRepository;
    private TradingTradeRecordRepositoryAdapter adapter;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @BeforeEach
    public void setUp() {
        mapper = new TradingTradeDataAccessMapper();
        adapter = new TradingTradeRecordRepositoryAdapter(tradeJpaRepository,jpaQueryFactory, mapper);
    }

    private TradeId tradeId = new TradeId(UUID.randomUUID());
    private OrderId sellOrderId = OrderId.anonymous();
    private OrderId buyOrderId = OrderId.anonymous();
    private UserId userId = new UserId(UUID.randomUUID());
    private MarketId marketId = new MarketId("KRW-BTC");
    private OrderPrice orderPrice = new OrderPrice(BigDecimal.valueOf(1_000_000));
    private Quantity quantity = new Quantity(BigDecimal.valueOf(1_0L));
    private TransactionType transactionType = TransactionType.TRADE_BUY;
    private FeeAmount feeAmount = new FeeAmount(BigDecimal.valueOf(1_000));
    private FeeRate feeRate = new FeeRate(BigDecimal.valueOf(0.3));
    @Test
    @DisplayName("거래 기록 생성 테스트")
    public void saveTradeTest() {
        // given
        Trade trade = Trade.createTrade(tradeId, marketId, userId, buyOrderId, orderPrice,
                quantity, transactionType, feeAmount, feeRate);
        // when
        Trade saved = adapter.saveTrade(trade);
        // then
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(tradeId, saved.getId());
        Assertions.assertEquals(marketId, saved.getMarketId());
        Assertions.assertEquals(userId, saved.getUserId());
        Assertions.assertEquals(buyOrderId, saved.getBuyOrderId());
        Assertions.assertEquals(orderPrice, saved.getOrderPrice());
        Assertions.assertEquals(quantity, saved.getQuantity());
        Assertions.assertEquals(transactionType, saved.getTransactionType());
        Assertions.assertEquals(feeAmount, saved.getFeeAmount());
        Assertions.assertEquals(feeRate, saved.getFeeRate());
    }
}
