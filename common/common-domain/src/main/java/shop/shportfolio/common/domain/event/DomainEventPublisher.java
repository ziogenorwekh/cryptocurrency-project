package shop.shportfolio.common.domain.event;

public interface DomainEventPublisher<D extends DomainEvent> {

    void publish(D domainEvent);
}
