package shop.shportfolio.trading.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.domain.entity.LimitOrder;

@Service
@Validated
public class TradingApplicationServiceImpl implements TradingApplicationService {

    private final TradingCreateOrderUseCase  createOrderUseCase;
    private final TradingDataMapper tradingDataMapper;
    @Autowired
    public TradingApplicationServiceImpl(TradingCreateOrderUseCase createOrderUseCase, TradingDataMapper tradingDataMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.tradingDataMapper = tradingDataMapper;
    }

    @Override
    public CreateLimitOrderResponse createLimitOrder(CreateLimitOrderCommand createLimitOrderCommand) {
        LimitOrder limitOrder = createOrderUseCase.createLimitOrder(createLimitOrderCommand);
        return tradingDataMapper.LimitOrderToCreateLimitOrderResponse(limitOrder);
    }
}
