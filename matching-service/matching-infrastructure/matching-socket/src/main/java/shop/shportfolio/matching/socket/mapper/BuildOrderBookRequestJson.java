package shop.shportfolio.matching.socket.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.helper.MarketHardCodingData;
import shop.shportfolio.matching.socket.config.BuildSocketData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class BuildOrderBookRequestJson {

    private final ObjectMapper mapper;

    public BuildOrderBookRequestJson(ObjectMapper mapper) {
        this.mapper = mapper.copy();
    }

    public String buildOrderBook() {
        List<Map<String, Object>> request = new ArrayList<>();
        request.add(Map.of(BuildSocketData.ticket, UUID.randomUUID().toString()));
        request.add(Map.of(BuildSocketData.type, BuildSocketData.orderbook, BuildSocketData.codes,
                MarketHardCodingData.marketList));
        request.add(Map.of(BuildSocketData.format, BuildSocketData.defaultType));
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
