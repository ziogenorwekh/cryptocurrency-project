package shop.shportfolio.trading.infrastructure.database.jpa.test;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.infrastructure.database.jpa.adapter.OrderRepositoryAdapter;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingOrderDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.LimitOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.MarketOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.ReservationOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.test.config.TestConfig;

import java.math.BigDecimal;
import java.util.UUID;

@DataJpaTest
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.yml")
public class OrderDatabaseTest {

    @Autowired
    private LimitOrderJpaRepository limitOrderJpaRepository;
    @Autowired
    private ReservationOrderJpaRepository reservationOrderJpaRepository;
    @Autowired
    private MarketOrderJpaRepository marketOrderJpaRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    private TradingOrderDataAccessMapper mapper;
    private OrderRepositoryAdapter repositoryAdapter;

    private UserId userId = new  UserId(UUID.randomUUID());
    private MarketId marketId = new MarketId("KRW-BTC");
    private OrderSide orderSide = OrderSide.BUY;
    private OrderPrice orderPrice = new OrderPrice(BigDecimal.valueOf(1_000_000));
    private OrderType limitOrderType = OrderType.LIMIT;
    @BeforeEach
    public void setup() {
        mapper = new TradingOrderDataAccessMapper();
        repositoryAdapter = new OrderRepositoryAdapter(jpaQueryFactory,mapper
        ,limitOrderJpaRepository,reservationOrderJpaRepository,marketOrderJpaRepository);
    }

    @Test
    @DisplayName("시장가 주문이 제대로 저장되는지 확인하는 테스트")
    public void saveMarketOrderTest() {
        // given
        MarketOrder marketOrder = MarketOrder.createMarketOrder(userId, marketId,
                orderSide, orderPrice, limitOrderType);
        // when
        MarketOrder saved = repositoryAdapter.saveMarketOrder(marketOrder);
        // then
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(marketOrder, saved);
        Assertions.assertEquals(orderSide, saved.getOrderSide());
        Assertions.assertEquals(orderPrice, saved.getOrderPrice());
        Assertions.assertEquals(limitOrderType, saved.getOrderType());
        Assertions.assertEquals(userId, saved.getUserId());
        Assertions.assertEquals(marketId, saved.getMarketId());
    }


}
