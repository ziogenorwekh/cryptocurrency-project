package shop.shportfolio.portfolio.application.port.input;

import shop.shportfolio.portfolio.application.command.create.DepositCreateCommand;
import shop.shportfolio.portfolio.application.dto.DepositResultContext;

public interface DepositUseCase {

    DepositResultContext deposit(DepositCreateCommand depositCreateCommand);

    DepositResultContext depositMock(DepositCreateCommand depositCreateCommand);
}
