package shop.shportfolio.trading.infrastructure.bithumb.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.exception.BithumbAPIRequestException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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

    public OrderBookBithumbDto toOrderBookBithumbDto(String rawResponse) {
        checkErrorResponse(rawResponse);
        try {
            JsonNode node = objectMapper.readTree(rawResponse);
            if (!node.isArray() || node.size() == 0) {
                throw new IllegalArgumentException("Invalid response format");
            }
            JsonNode first = node.get(0);

            OrderBookBithumbDto dto = new OrderBookBithumbDto();
            dto.setMarket(first.get("market").asText());
            dto.setTimestamp(first.get("timestamp").asLong());
            dto.setTotalAskSize(first.get("total_ask_size").asDouble());
            dto.setTotalBidSize(first.get("total_bid_size").asDouble());
            JsonNode units = first.get("orderbook_units");
            List<OrderBookAsksBithumbDto> asks = new ArrayList<>();
            List<OrderBookBidsBithumbDto> bids = new ArrayList<>();

            for (var unit : units) {
                asks.add(new OrderBookAsksBithumbDto(
                        unit.get("ask_price").asDouble(),
                        unit.get("ask_size").asDouble()
                ));
                bids.add(new OrderBookBidsBithumbDto(
                        unit.get("bid_price").asDouble(),
                        unit.get("bid_size").asDouble()
                ));
            }
            dto.setAsks(asks);
            dto.setBids(bids);

            return dto;

        } catch (JsonProcessingException e) {
            log.error("jsonProcessingException is : {}", e.getMessage());
            throw new BithumbAPIRequestException("Failed to parse Bithumb orderbook JSON");
        }
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

    public MarketTickerResponseDto toMarketTickerResponseDto(String rawResponse) {
        checkErrorResponse(rawResponse);
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            if (!rootNode.isArray() || rootNode.isEmpty()) {
                throw new IllegalArgumentException("Response data is missing or empty");
            }

            JsonNode dataNode = rootNode.get(0);  // 배열 첫 번째 요소
            if (dataNode.isMissingNode() || dataNode.isNull()) {
                throw new IllegalArgumentException("Response data is missing");
            }

            return MarketTickerResponseDto.builder()
                    .market(dataNode.path("market").asText(null))
                    .tradeDate(dataNode.path("trade_date").asText(null))
                    .tradeTime(dataNode.path("trade_time").asText(null))
                    .tradeDateKst(dataNode.path("trade_date_kst").asText(null))
                    .tradeTimeKst(dataNode.path("trade_time_kst").asText(null))
                    .tradeTimestamp(asLongOrNull(dataNode, "trade_timestamp"))
                    .openingPrice(asBigDecimalOrNull(dataNode, "opening_price"))
                    .highPrice(asBigDecimalOrNull(dataNode, "high_price"))
                    .lowPrice(asBigDecimalOrNull(dataNode, "low_price"))
                    .tradePrice(asBigDecimalOrNull(dataNode, "trade_price"))
                    .prevClosingPrice(asBigDecimalOrNull(dataNode, "prev_closing_price"))
                    .change(dataNode.path("change").asText(null))
                    .changePrice(asBigDecimalOrNull(dataNode, "change_price"))
                    .changeRate(asBigDecimalOrNull(dataNode, "change_rate"))
                    .signedChangePrice(asBigDecimalOrNull(dataNode, "signed_change_price"))
                    .signedChangeRate(asBigDecimalOrNull(dataNode, "signed_change_rate"))
                    .tradeVolume(asBigDecimalOrNull(dataNode, "trade_volume"))
                    .accTradePrice(asBigDecimalOrNull(dataNode, "acc_trade_price"))
                    .accTradePrice24h(asBigDecimalOrNull(dataNode, "acc_trade_price_24h"))
                    .accTradeVolume(asBigDecimalOrNull(dataNode, "acc_trade_volume"))
                    .accTradeVolume24h(asBigDecimalOrNull(dataNode, "acc_trade_volume_24h"))
                    .highest52WeekPrice(asBigDecimalOrNull(dataNode, "highest_52_week_price"))
                    .highest52WeekDate(dataNode.path("highest_52_week_date").asText(null))
                    .lowest52WeekPrice(asBigDecimalOrNull(dataNode, "lowest_52_week_price"))
                    .lowest52WeekDate(dataNode.path("lowest_52_week_date").asText(null))
                    .timestamp(asLongOrNull(dataNode, "timestamp"))
                    .build();

        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException is -> {}", e.getMessage());
            return null;  // 또는 예외 재던지기
        }
    }

    public List<TradeTickResponseDto> toTradeTickResponseDtoList(String rawResponse) {
        checkErrorResponse(rawResponse);
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            if (!rootNode.isArray()) {
                throw new IllegalArgumentException("Expected JSON array");
            }
            List<TradeTickResponseDto> list = new ArrayList<>();
            for (JsonNode node : rootNode) {
                TradeTickResponseDto dto = TradeTickResponseDto.builder()
                        .market(node.path("market").asText(null))
                        .tradeDateUtc(node.path("trade_date_utc").asText(null))
                        .tradeTimeUtc(node.path("trade_time_utc").asText(null))
                        .timestamp(asLongOrNull(node, "timestamp"))
                        .tradePrice(asBigDecimalOrNull(node, "trade_price"))
                        .tradeVolume(asBigDecimalOrNull(node, "trade_volume"))
                        .prevClosingPrice(asBigDecimalOrNull(node, "prev_closing_price"))
                        .changePrice(asBigDecimalOrNull(node, "change_price"))  // 오타 'chane_price' 주의
                        .askBid(node.path("ask_bid").asText(null))
                        .sequentialId(asLongOrNull(node, "sequential_id"))
                        .build();
                list.add(dto);
            }
            return list;
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException is -> {}", e.getMessage());
            return Collections.emptyList();
        }
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

    private BigDecimal asBigDecimalOrNull(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return new BigDecimal(node.get(fieldName).asText()); // 문자열 그대로 보존
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
