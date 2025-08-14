package shop.shportfolio.marketdata.insight.infrastructure.bithumb.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.marketdata.insight.application.exception.BithumbAPIRequestException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BithumbApiMapper {

    private final ObjectMapper objectMapper;

    @Autowired
    public BithumbApiMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new JavaTimeModule());
    }


    public List<MarketItemBithumbDto> toMarketItemBithumbDtoList(String rawResponse) {
        checkErrorResponse(rawResponse);
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            if (!rootNode.isArray()) {
                throw new IllegalArgumentException("Expected JSON array");
            }

            List<MarketItemBithumbDto> list = new ArrayList<>();
            for (JsonNode node : rootNode) {
                MarketItemBithumbDto dto = MarketItemBithumbDto.builder()
                        .marketId(node.get("market").asText())
                        .koreanName(node.get("korean_name").asText())
                        .englishName(node.get("english_name").asText())
                        .build();
                list.add(dto);
            }
            return list;
        } catch (JsonProcessingException e) {
            log.error("jsonProcessingException is : {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<CandleDayResponseDto> toCandleDayResponseDtoList(String rawResponse) {
        checkErrorResponse(rawResponse);
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            if (!rootNode.isArray()) {
                throw new IllegalArgumentException("Expected JSON array");
            }
            List<CandleDayResponseDto> list = new ArrayList<>();
            for (JsonNode node : rootNode) {
                CandleDayResponseDto dto = new CandleDayResponseDto(
                        node.path("market").asText(null),
                        node.path("candle_date_time_utc").asText(null),
                        node.path("candle_date_time_kst").asText(null),
                        asDoubleOrNull(node, "opening_price"),
                        asDoubleOrNull(node, "high_price"),
                        asDoubleOrNull(node, "low_price"),
                        asDoubleOrNull(node, "trade_price"),
                        asDoubleOrNull(node, "candle_acc_trade_price"),
                        asDoubleOrNull(node, "candle_acc_trade_volume"),
                        asDoubleOrNull(node, "prev_closing_price"),
                        asDoubleOrNull(node, "change_price"),
                        asDoubleOrNull(node, "change_rate")
                );
                list.add(dto);
            }
            return list;
        } catch (JsonProcessingException e) {
            log.error("jsonProcessingException is : {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<CandleWeekResponseDto> toCandleWeekResponseDto(String rawResponse) {
        checkErrorResponse(rawResponse);
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            if (!rootNode.isArray()) {
                throw new IllegalArgumentException("Expected JSON array");
            }
            List<CandleWeekResponseDto> list = new ArrayList<>();
            for (JsonNode node : rootNode) {
                CandleWeekResponseDto dto = new CandleWeekResponseDto(
                        node.path("market").asText(null),
                        node.path("candle_date_time_utc").asText(null),
                        node.path("candle_date_time_kst").asText(null),
                        asDoubleOrNull(node, "opening_price"),
                        asDoubleOrNull(node, "high_price"),
                        asDoubleOrNull(node, "low_price"),
                        asDoubleOrNull(node, "trade_price"),
                        asLongOrNull(node, "timestamp"),
                        asDoubleOrNull(node, "candle_acc_trade_price"),
                        asDoubleOrNull(node, "candle_acc_trade_volume"),
                        node.path("first_day_of_period").asText(null)
                );
                list.add(dto);
            }
            return list;
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException is -> {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<CandleMonthResponseDto> toCandleMonthResponseDtoList(String rawResponse) {
        checkErrorResponse(rawResponse);
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            if (!rootNode.isArray()) {
                throw new IllegalArgumentException("Expected JSON array");
            }
            List<CandleMonthResponseDto> list = new ArrayList<>();
            for (JsonNode node : rootNode) {
                CandleMonthResponseDto dto = new CandleMonthResponseDto(
                        node.path("market").asText(null),
                        node.path("candle_date_time_utc").asText(null),
                        node.path("candle_date_time_kst").asText(null),
                        asDoubleOrNull(node, "opening_price"),
                        asDoubleOrNull(node, "high_price"),
                        asDoubleOrNull(node, "low_price"),
                        asDoubleOrNull(node, "trade_price"),
                        asLongOrNull(node, "timestamp"),
                        asDoubleOrNull(node, "candle_acc_trade_price"),
                        asDoubleOrNull(node, "candle_acc_trade_volume"),
                        node.path("first_day_of_period").asText(null)
                );
                list.add(dto);
            }
            return list;
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException is -> {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<CandleMinuteResponseDto> toCandleMinuteResponseDtoList(String rawResponse) {
        checkErrorResponse(rawResponse);
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            if (!rootNode.isArray()) {
                throw new IllegalArgumentException("Expected JSON array");
            }
            List<CandleMinuteResponseDto> list = new ArrayList<>();
            for (JsonNode node : rootNode) {
                CandleMinuteResponseDto dto = new CandleMinuteResponseDto(
                        node.path("market").asText(null),
                        node.path("candle_date_time_utc").asText(null),
                        node.path("candle_date_time_kst").asText(null),
                        asDoubleOrNull(node, "opening_price"),
                        asDoubleOrNull(node, "high_price"),
                        asDoubleOrNull(node, "low_price"),
                        asDoubleOrNull(node, "trade_price"),
                        asLongOrNull(node, "timestamp"),
                        asDoubleOrNull(node, "candle_acc_trade_price"),
                        asDoubleOrNull(node, "candle_acc_trade_volume"),
                        asIntegerOrNull(node, "unit")
                );
                list.add(dto);
            }
            return list;
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException is -> {}", e.getMessage());
            return new ArrayList<>();
        }
    }


    private Integer asIntegerOrNull(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asInt();
        }
        return null;
    }

    private Long asLongOrNull(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asLong();
        }
        return null;
    }
    private Double asDoubleOrNull(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asDouble();
        }
        return null;
    }

    private void checkErrorResponse(String rawResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode errorNode = rootNode.path("error");
            if (!errorNode.isMissingNode() && !errorNode.isNull()) {
                int errorCode = errorNode.path("name").asInt(-1);
                String errorMessage = errorNode.path("message").asText("Unknown error");
                if (errorCode == 400) {
                    throw new BithumbAPIRequestException("Bithumb API Error 400: " + errorMessage);
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse error response JSON: {}", e.getMessage());
        }
    }

}
