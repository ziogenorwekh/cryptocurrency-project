package shop.shportfolio.marketdata.insight.infrastructure.bithumb.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.marketdata.insight.application.exception.BithumbAPIRequestException;

import java.math.BigDecimal;
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
                        asBigDecimalOrNull(node, "opening_price"),
                        asBigDecimalOrNull(node, "high_price"),
                        asBigDecimalOrNull(node, "low_price"),
                        asBigDecimalOrNull(node, "trade_price"),
                        asBigDecimalOrNull(node, "candle_acc_trade_price"),
                        asBigDecimalOrNull(node, "candle_acc_trade_volume"),
                        asBigDecimalOrNull(node, "prev_closing_price"),
                        asBigDecimalOrNull(node, "change_price"),
                        asBigDecimalOrNull(node, "change_rate")
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
                        asBigDecimalOrNull(node, "opening_price"),
                        asBigDecimalOrNull(node, "high_price"),
                        asBigDecimalOrNull(node, "low_price"),
                        asBigDecimalOrNull(node, "trade_price"),
                        asLongOrNull(node, "timestamp"),
                        asBigDecimalOrNull(node, "candle_acc_trade_price"),
                        asBigDecimalOrNull(node, "candle_acc_trade_volume"),
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
                        asBigDecimalOrNull(node, "opening_price"),
                        asBigDecimalOrNull(node, "high_price"),
                        asBigDecimalOrNull(node, "low_price"),
                        asBigDecimalOrNull(node, "trade_price"),
                        asLongOrNull(node, "timestamp"),
                        asBigDecimalOrNull(node, "candle_acc_trade_price"),
                        asBigDecimalOrNull(node, "candle_acc_trade_volume"),
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
                        asBigDecimalOrNull(node, "opening_price"),
                        asBigDecimalOrNull(node, "high_price"),
                        asBigDecimalOrNull(node, "low_price"),
                        asBigDecimalOrNull(node, "trade_price"),
                        asLongOrNull(node, "timestamp"),
                        asBigDecimalOrNull(node, "candle_acc_trade_price"),
                        asBigDecimalOrNull(node, "candle_acc_trade_volume"),
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

    private BigDecimal asBigDecimalOrNull(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return new BigDecimal(node.get(fieldName).asText());
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
