package shop.shportfolio.trading.socket.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.scheduler.MarketHardCodingData;
import shop.shportfolio.trading.socket.config.BuildSocketData;

import java.util.*;

@Component
public class BuildRequestJson {

    private final ObjectMapper mapper;

    public BuildRequestJson(ObjectMapper mapper) {
        this.mapper = mapper.copy();
    }

    public String buildTicker() {
        List<Map<String, Object>> request = new ArrayList<>();
        Map<String, Object> ticket = new HashMap<>();
        ticket.put(BuildSocketData.ticket, UUID.randomUUID().toString());
        request.add(ticket);

        Map<String, Object> type = new HashMap<>();
        type.put(BuildSocketData.type, BuildSocketData.ticker);
        type.put(BuildSocketData.codes, MarketHardCodingData.marketMap.keySet().stream().toList());
        type.put(BuildSocketData.format, BuildSocketData.defaultType); // format을 여기 안으로 넣음
        request.add(type);
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String buildTrade() {
        List<Map<String, Object>> request = new ArrayList<>();
        Map<String, Object> ticket = new HashMap<>();
        ticket.put(BuildSocketData.ticket, UUID.randomUUID().toString());
        request.add(ticket);

        Map<String, Object> type = new HashMap<>();
        type.put(BuildSocketData.type, BuildSocketData.trade);
        type.put(BuildSocketData.codes, MarketHardCodingData.marketMap.keySet().stream().toList());
        type.put(BuildSocketData.format, BuildSocketData.defaultType); // format을 여기 안으로 넣음
        request.add(type);
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
