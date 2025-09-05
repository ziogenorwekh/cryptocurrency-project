package shop.shportfolio.matching.socket.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.application.exception.BithumbApiException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Component
public class MatchingSocketDataMapper {

    private final ObjectMapper objectMapper;

    @Autowired
    public MatchingSocketDataMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
    }

    public OrderBookBithumbDto toOrderBookBithumbDto(String rawResponse) {
        checkErrorResponse(rawResponse);

        try {
            JsonNode node = objectMapper.readTree(rawResponse);

            // 배열이면 첫번째, 아니면 자기 자신
            JsonNode first = node.isArray() && node.size() > 0 ? node.get(0) : node;

            OrderBookBithumbDto dto = new OrderBookBithumbDto();
            dto.setMarket(first.path("code").asText());
            dto.setTimestamp(first.path("timestamp").asLong());
            dto.setTotalAskSize(first.path("total_ask_size").asDouble());
            dto.setTotalBidSize(first.path("total_bid_size").asDouble());

            JsonNode units = first.path("orderbook_units");
            List<OrderBookAsksBithumbDto> asks = new ArrayList<>();
            List<OrderBookBidsBithumbDto> bids = new ArrayList<>();

            for (JsonNode unit : units) {
                asks.add(new OrderBookAsksBithumbDto(
                        unit.path("ask_price").asDouble(),
                        unit.path("ask_size").asDouble()
                ));
                bids.add(new OrderBookBidsBithumbDto(
                        unit.path("bid_price").asDouble(),
                        unit.path("bid_size").asDouble()
                ));
            }
            dto.setAsks(asks);
            dto.setBids(bids);
            if (asks.size() > 1) {
                BigDecimal firstAsk = BigDecimal.valueOf(asks.get(0).getAskPrice());
                BigDecimal secondAsk = BigDecimal.valueOf(asks.get(1).getAskPrice());
                BigDecimal tickPrice = firstAsk.subtract(secondAsk).abs();
                dto.setTickPrice(tickPrice.setScale(0, RoundingMode.HALF_UP).doubleValue());
            } else {
                dto.setTickPrice(0.0);
            }
            return dto;

        } catch (JsonProcessingException e) {
            log.error("jsonProcessingException is : {}", e.getMessage());
            throw new BithumbApiException("Failed to parse Bithumb orderbook JSON");
        }
    }

    private void checkErrorResponse(String rawResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode errorNode = rootNode.path("error");
            if (!errorNode.isMissingNode() && !errorNode.isNull()) {
                String errorName = errorNode.path("name").asText();
                String errorMessage = errorNode.path("message").asText("Unknown error");
                throw new BithumbApiException("Bithumb API Error [" + errorName + "]: " + errorMessage);
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse error response JSON: {}", e.getMessage());
            throw new BithumbApiException("Failed to parse Bithumb orderbook JSON");
        }
    }
}
