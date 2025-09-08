package shop.shportfolio.matching.socket.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.helper.MarketHardCodingData;
import shop.shportfolio.matching.socket.config.BuildSocketData;

import java.util.*;

@Component
public class BuildOrderBookRequestJson {

    private final ObjectMapper mapper;

    public BuildOrderBookRequestJson(ObjectMapper mapper) {
        this.mapper = mapper.copy();
    }

    public String buildOrderBook(Double level) {
        List<Map<String, Object>> request = new ArrayList<>();
        Map<String, Object> ticket = new HashMap<>();
        ticket.put(BuildSocketData.ticket, UUID.randomUUID().toString());
        request.add(ticket);

        Map<String, Object> type = new HashMap<>();
        type.put(BuildSocketData.type, BuildSocketData.orderbook);
        type.put(BuildSocketData.codes, MarketHardCodingData.marketMap.keySet().stream().toList());
        type.put(BuildSocketData.level, 1.0); // Double/int 가능
        type.put(BuildSocketData.format, BuildSocketData.defaultType); // format을 여기 안으로 넣음
        request.add(type);
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
