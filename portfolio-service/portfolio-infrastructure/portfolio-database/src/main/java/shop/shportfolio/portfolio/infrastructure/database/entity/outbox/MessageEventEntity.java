package shop.shportfolio.portfolio.infrastructure.database.entity.outbox;

import jakarta.persistence.*;
import lombok.*;
import shop.shportfolio.common.domain.valueobject.OutBoxStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_message_event",
        indexes = {
                @Index(name = "idx_status_created_at", columnList = "outBoxStatus, createdAt")
        })
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AGGREGATE_ID",nullable = false)
    private String aggregateId;

    @Column(name = "AGGREGATE_TYPE",nullable = false)
    private String aggregateType;

    @Column(name = "TOPIC_NAME", nullable = false)
    private String topicName;

    @Column(name = "KAFKA_KEY", nullable = false)
    private String kafkaKey;

    @Lob
    @Column(name = "PAYLOAD", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "OUTBOX_STATUS", nullable = false)
    private OutBoxStatus outBoxStatus;

    public void sent() {
        this.outBoxStatus = OutBoxStatus.SENT;
    }

    public void failed() {
        this.outBoxStatus = OutBoxStatus.FAILED;
    }

}