package shop.shportfolio.portfolio.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioCreateCommand {

    private UUID userId;
}
