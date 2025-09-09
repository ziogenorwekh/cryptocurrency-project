package shop.shportfolio.user.infrastructure.kafka.publisher.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.avro.UserIdAvroModel;
import shop.shportfolio.common.domain.valueobject.UserId;

import java.util.UUID;

@Component
public class UserMessageMapper {

    public UserIdAvroModel toUserIdAvroModel(String userId,
                                             shop.shportfolio.common.domain.valueobject.MessageType messageType) {
        return UserIdAvroModel.newBuilder()
                .setUserId(userId)
                .setMessageType(toMessageType(messageType))
                .build();
    }

    private MessageType toMessageType(shop.shportfolio.common.domain.valueobject.MessageType messageType) {
        return switch (messageType) {
            case CREATE -> MessageType.CREATE;
            case DELETE -> MessageType.DELETE;
            case FAIL -> MessageType.FAIL;
            case REJECT -> MessageType.REJECT;
            case UPDATE -> MessageType.UPDATE;
            case NO_DEF -> MessageType.NO_DEF;
        };
    }
}
