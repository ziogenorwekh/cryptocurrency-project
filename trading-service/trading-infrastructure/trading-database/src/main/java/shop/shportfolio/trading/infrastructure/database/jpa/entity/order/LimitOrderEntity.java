package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "LIMIT_ORDER")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("limit")
@PrimaryKeyJoinColumn(name = "LIMIT_ORDER_ID")
public class LimitOrderEntity extends OrderEntity {

}
