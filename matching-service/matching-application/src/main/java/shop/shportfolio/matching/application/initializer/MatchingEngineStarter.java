package shop.shportfolio.matching.application.initializer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.kafka.MatchingEngineStartPublisher;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Slf4j
@Component
public class MatchingEngineStarter {

    private final MatchingEngineStartPublisher matchingEngineStartPublisher;
    public MatchingEngineStarter(
                                 MatchingEngineStartPublisher matchingEngineStartPublisher) {
        this.matchingEngineStartPublisher = matchingEngineStartPublisher;
    }

    @PostConstruct
    public void init() {
        matchingEngineStartPublisher.publish(new DomainEvent(null, null,
                ZonedDateTime.now(ZoneOffset.UTC)) {
            @Override
            public Object getDomainType() {
                return super.getDomainType();
            }
        });
        log.info("Matching engine start event published");
    }

}
