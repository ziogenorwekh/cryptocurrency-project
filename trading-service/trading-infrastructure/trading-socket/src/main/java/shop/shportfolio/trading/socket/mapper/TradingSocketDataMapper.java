package shop.shportfolio.trading.socket.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.track.response.TradeTickTrackResponse;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.exception.BithumbAPIRequestException;
import shop.shportfolio.trading.application.exception.BithumbSocketException;

@Component
public class TradingSocketDataMapper {

    private final ObjectMapper objectMapper;

    public TradingSocketDataMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
    }

    public MarketTickerResponseDto toMarketTickerResponseDto(String rawResponse) {
        checkErrorResponse(rawResponse);

        try {
            JsonNode node = objectMapper.readTree(rawResponse);

            // 배열이면 첫 번째, 아니면 자기 자신
            JsonNode first = node.isArray() && node.size() > 0 ? node.get(0) : node;

            return MarketTickerResponseDto.builder()
                    .market(first.path("code").asText())
                    .tradeDate(first.path("trade_date").asText())
                    .tradeTime(first.path("trade_time").asText())
                    .tradeDateKst(first.path("trade_date_kst").asText())
                    .tradeTimeKst(first.path("trade_time_kst").asText())
                    .tradeTimestamp(first.path("trade_timestamp").asLong())
                    .openingPrice(first.path("opening_price").asDouble())
                    .highPrice(first.path("high_price").asDouble())
                    .lowPrice(first.path("low_price").asDouble())
                    .tradePrice(first.path("trade_price").asDouble())
                    .prevClosingPrice(first.path("prev_closing_price").asDouble())
                    .change(first.path("change").asText())
                    .changePrice(first.path("change_price").asDouble())
                    .changeRate(first.path("change_rate").asDouble())
                    .signedChangePrice(first.path("signed_change_price").asDouble())
                    .signedChangeRate(first.path("signed_change_rate").asDouble())
                    .tradeVolume(first.path("trade_volume").asDouble())
                    .accTradePrice(first.path("acc_trade_price").asDouble())
                    .accTradePrice24h(first.path("acc_trade_price_24h").asDouble())
                    .accTradeVolume(first.path("acc_trade_volume").asDouble())
                    .accTradeVolume24h(first.path("acc_trade_volume_24h").asDouble())
                    .highest52WeekPrice(first.path("highest_52_week_price").asDouble())
                    .highest52WeekDate(first.path("highest_52_week_date").asText())
                    .lowest52WeekPrice(first.path("lowest_52_week_price").asDouble())
                    .lowest52WeekDate(first.path("lowest_52_week_date").asText())
                    .timestamp(first.path("timestamp").asLong())
                    .build();

        } catch (JsonProcessingException e) {
            throw new BithumbSocketException("Failed to parse Bithumb ticker JSON: " + e.getMessage());
        }
    }

    public TradeTickResponseDto toTradeTickResponseDto(String rawResponse) {
        checkErrorResponse(rawResponse);

        try {
            JsonNode node = objectMapper.readTree(rawResponse);
            JsonNode first = node.isArray() && node.size() > 0 ? node.get(0) : node;

            return TradeTickResponseDto.builder()
                    .market(first.path("code").asText())
                    .tradeDateUtc(first.path("trade_date").asText())
                    .tradeTimeUtc(first.path("trade_time").asText())
                    .timestamp(first.path("timestamp").asLong())
                    .tradePrice(first.path("trade_price").asDouble())
                    .tradeVolume(first.path("trade_volume").asDouble())
                    .prevClosingPrice(first.path("prev_closing_price").asDouble())
                    .changePrice(first.path("change_price").asDouble())
                    .askBid(first.path("ask_bid").asText())
                    .sequentialId(first.path("sequential_id").asLong())
                    .build();

        } catch (JsonProcessingException e) {
            throw new BithumbSocketException("Failed to parse Bithumb trade JSON: " + e.getMessage());
        }
    }

    private void checkErrorResponse(String rawResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode errorNode = rootNode.path("error");
            if (!errorNode.isMissingNode() && !errorNode.isNull()) {
                String errorName = errorNode.path("name").asText();
                String errorMessage = errorNode.path("message").asText("Unknown error");
                throw new BithumbSocketException("Bithumb API Error [" + errorName + "]: " + errorMessage);
            }
        } catch (JsonProcessingException e) {
            throw new BithumbSocketException("Failed to parse Bithumb ticker JSON");
        }
    }

}
