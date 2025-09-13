package shop.shportfolio.trading.infrastructure.database.jpa.test;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;
import shop.shportfolio.trading.infrastructure.database.jpa.adapter.OrderRepositoryAdapter;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingOrderDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.LimitOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.MarketOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.ReservationOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.test.config.TestConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
    private OrderType marketOrderType = OrderType.MARKET;
    private Quantity quantity = new Quantity(BigDecimal.valueOf(1_00));
    @BeforeEach
    public void setup() {
        mapper = new TradingOrderDataAccessMapper();
        repositoryAdapter = new OrderRepositoryAdapter(mapper
        ,limitOrderJpaRepository,reservationOrderJpaRepository,marketOrderJpaRepository);
    }

    @Test
    @DisplayName("시장가 주문이 제대로 저장되는지 확인하는 테스트")
    public void saveMarketOrderTest() {
        // given
        MarketOrder marketOrder = MarketOrder.createMarketOrder(userId, marketId,
                orderSide, null, orderPrice, marketOrderType);
        // when
        MarketOrder saved = repositoryAdapter.saveMarketOrder(marketOrder);
        // then
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(marketOrder, saved);
        Assertions.assertEquals(orderSide, saved.getOrderSide());
        Assertions.assertEquals(orderPrice, saved.getOrderPrice());
        Assertions.assertEquals(marketOrderType, saved.getOrderType());
        Assertions.assertEquals(userId, saved.getUserId());
        Assertions.assertEquals(marketId, saved.getMarketId());
    }

    @Test
    @DisplayName("지정가 주문이 제대로 저장되는지 테스트")
    public void saveLimitOrderTest() {
        // given
        LimitOrder limitOrder = LimitOrder.createLimitOrder(userId, marketId,
                orderSide, quantity, orderPrice, limitOrderType);
        // when
        LimitOrder saved = repositoryAdapter.saveLimitOrder(limitOrder);
        // then
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(limitOrder, saved);
        Assertions.assertEquals(orderSide, saved.getOrderSide());
        Assertions.assertEquals(orderPrice, saved.getOrderPrice());
        Assertions.assertEquals(limitOrderType, saved.getOrderType());
        Assertions.assertEquals(userId, saved.getUserId());
        Assertions.assertEquals(marketId, saved.getMarketId());
        Assertions.assertEquals(orderSide, saved.getOrderSide());
    }

    @Test
    @DisplayName("예약 주문이 제대로 저장되는지 테스트")
    public void saveReservationOrderTest() {
        // given
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(userId, marketId, orderSide, quantity, OrderType.RESERVATION
                , new TriggerCondition(TriggerType.ABOVE, new OrderPrice(BigDecimal.valueOf(10_500_000))),
                new ScheduledTime(LocalDateTime.now(ZoneOffset.UTC).plusDays(2)),
                new ExpireAt(LocalDateTime.now(ZoneOffset.UTC).plusDays(10)), new IsRepeatable(true));
        // when
        ReservationOrder saved = repositoryAdapter.saveReservationOrder(reservationOrder);
        // then
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(reservationOrder, saved);
        Assertions.assertEquals(orderSide, saved.getOrderSide());
        Assertions.assertEquals(OrderType.RESERVATION, saved.getOrderType());
        Assertions.assertEquals(userId, saved.getUserId());
        Assertions.assertEquals(marketId, saved.getMarketId());
        Assertions.assertEquals(TriggerType.ABOVE,saved.getTriggerCondition().getValue());
    }
}
