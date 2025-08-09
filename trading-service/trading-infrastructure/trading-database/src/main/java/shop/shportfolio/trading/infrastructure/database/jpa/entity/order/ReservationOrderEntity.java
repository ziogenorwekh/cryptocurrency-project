package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.valuetype.TriggerCondition;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "RESERVATION_ORDER")
@DiscriminatorValue("reservation")
@PrimaryKeyJoinColumn(name = "RESERVATION_ORDER_ID")
public class ReservationOrderEntity extends OrderEntity {

    @Embedded
    private TriggerCondition triggerCondition;

    @Column(name = "IS_REPEATABLE", nullable = false)
    private Boolean isRepeatable;

    @Column(name = "SCHEDULED_TIME", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "EXPIRE_AT", nullable = false)
    private LocalDateTime expireAt;


}
