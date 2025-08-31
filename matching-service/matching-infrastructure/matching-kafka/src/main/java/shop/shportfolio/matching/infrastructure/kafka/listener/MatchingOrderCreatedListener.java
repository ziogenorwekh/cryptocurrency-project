package shop.shportfolio.matching.infrastructure.kafka.listener;

import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.matching.application.ports.input.kafka.CreatedOrderListener;
import shop.shportfolio.matching.infrastructure.kafka.mapper.MatchingMessageMapper;

import java.util.List;

public class MatchingOrderCreatedListener{

    private final CreatedOrderListener createdOrderListener;
    private final MatchingMessageMapper matchingMessageMapper;

    public MatchingOrderCreatedListener(CreatedOrderListener createdOrderListener, MatchingMessageMapper matchingMessageMapper) {
        this.createdOrderListener = createdOrderListener;
        this.matchingMessageMapper = matchingMessageMapper;
    }

}
