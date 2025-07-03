package shop.shportfolio.coupon.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTrackQuery {

    private UUID userId;
    private UUID paymentId;
}
