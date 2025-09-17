package shop.shportfolio.trading.socket.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Mono;
import shop.shportfolio.trading.application.command.track.response.TradeTickTrackResponse;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.socket.handler.TickerWebSocketHandler;
import shop.shportfolio.trading.socket.TickerSenderImpl;
import shop.shportfolio.trading.socket.TradeSenderImpl;
import shop.shportfolio.trading.socket.adapter.BithumbTickerSocketClient;
import shop.shportfolio.trading.socket.adapter.BithumbTradeSocketClient;
import shop.shportfolio.trading.socket.config.SocketData;
import shop.shportfolio.trading.socket.config.WebSocketConfiguration;
import shop.shportfolio.trading.socket.handler.TradeWebSocketHandler;
import shop.shportfolio.trading.socket.mapper.BuildRequestJson;
import shop.shportfolio.trading.socket.mapper.TradingSocketDataMapper;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ActiveProfiles("test")
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER, classes = {WebSocketConfiguration.class
        , TickerWebSocketHandler.class, TickerSenderImpl.class, TradeSenderImpl.class, BithumbTickerSocketClient.class,
        BithumbTradeSocketClient.class, TradingSocketDataMapper.class,
        BuildRequestJson.class, SocketData.class, TradeWebSocketHandler.class,TickerWebSocketHandler.class})
public class BithumbSocketClientTest {

    @Autowired
    private BithumbTickerSocketClient bithumbTickerSocketClient;

    @Autowired
    private BithumbTradeSocketClient bithumbTradeSocketClient;

    @Autowired
    private SocketData socketData;

    @Autowired
    private TradingSocketDataMapper tradingSocketDataMapper;

    @Test
    public void socketTickerTest() throws InterruptedException {
        // given
        List<MarketTickerResponseDto> receivedList = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(5); // 최소 1개 메시지 받을 때까지 대기

        // 리스너 등록
        bithumbTickerSocketClient.setTickerListener((dto) -> {
            receivedList.add(dto);  // DTO 저장
            System.out.println("Received DTO: " + dto);
            latch.countDown();      // 메시지 수신 후 latch 해제
        });
        // when


        bithumbTickerSocketClient.connect();
        // 최대 3초 기다림
        boolean success = latch.await(10, TimeUnit.SECONDS);
        if (!success) {
            System.out.println("No messages received within 3 seconds");
        } else {
            System.out.println("Total messages received: " + receivedList.size());
        }
        // then
        bithumbTickerSocketClient.disconnect();

    }
    @Test
    public void socketTradeTest() throws InterruptedException {
        // given
        List<TradeTickResponseDto> receivedList = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(5); // 최소 1개 메시지 받을 때까지 대기

        // 리스너 등록
        bithumbTradeSocketClient.setTradeListener((dto) -> {
            receivedList.add(dto);  // DTO 저장
            System.out.println("Received DTO: " + dto);
            latch.countDown();      // 메시지 수신 후 latch 해제
        });
        // when


        bithumbTradeSocketClient.connect();
        // 최대 3초 기다림
        boolean success = latch.await(10, TimeUnit.SECONDS);
        if (!success) {
            System.out.println("No messages received within 3 seconds");
        } else {
            System.out.println("Total messages received: " + receivedList.size());
        }
        // then
        bithumbTradeSocketClient.disconnect();
    }

    @Disabled
    @Test
    void connectAndReceiveTickerSimpleTest() {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        URI uri = URI.create(socketData.getBithumbSocketUrl());

        client.execute(uri, session -> {
            // JSON 요청 객체 구성
            List<Map<String, Object>> request = new ArrayList<>();

            Map<String, Object> ticket = new HashMap<>();
            ticket.put("ticket", "test example");
            request.add(ticket);

            Map<String, Object> type = new HashMap<>();
            type.put("type", "trade");
            type.put("codes", Arrays.asList("KRW-BCH","KRW-BTC"));
            type.put("format", "DEFAULT"); // format을 여기 안으로 넣음
            request.add(type);

            ObjectMapper mapper = new ObjectMapper();
            String req;
            try {
                req = mapper.writeValueAsString(request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 요청 보내고 응답 1개 이상 받을 때까지 출력
            return session.send(Mono.just(session.textMessage(req)))
                    .thenMany(session.receive()
                            .map(msg -> msg.getPayloadAsText())
                            .doOnNext(payload -> {
                                TradeTickResponseDto dto = tradingSocketDataMapper.
                                        toTradeTickResponseDto(payload);
                                System.out.println("dto = " + dto);
                            })
                            .take(5) // 메시지 5개까지만 받고 끊기
                    )
                    .then();
        }).block(Duration.ofSeconds(30)); // 최대 20초 안에 끝내기
    }
}
