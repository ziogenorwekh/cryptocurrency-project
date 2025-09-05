package shop.shportfolio.matching.socket.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Mono;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.socket.OrderBookSenderImpl;
import shop.shportfolio.matching.socket.OrderBookWebSocketHandler;
import shop.shportfolio.matching.socket.adapter.BithumbOrderBookSocketClient;
import shop.shportfolio.matching.socket.config.SocketData;
import shop.shportfolio.matching.socket.config.WebSocketConfiguration;
import shop.shportfolio.matching.socket.mapper.BuildOrderBookRequestJson;
import shop.shportfolio.matching.socket.mapper.MatchingSocketDataMapper;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@ActiveProfiles("test")
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER,classes = {WebSocketConfiguration.class
, OrderBookWebSocketHandler.class, OrderBookSenderImpl.class, BithumbOrderBookSocketClient.class,
MatchingSocketDataMapper.class, BuildOrderBookRequestJson.class})
public class BithumbSocketTest {

    @Autowired
    private BithumbOrderBookSocketClient clientAdapter;

    @Autowired
    private SocketData socketData;

    @Autowired
    private MatchingSocketDataMapper matchingSocketDataMapper;

    @Test
    void connectAndReceiveOrderBookMyMethodTest() throws InterruptedException {
        List<OrderBookBithumbDto> receivedList = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1); // 최소 1개 메시지 받을 때까지 대기

        // 리스너 등록
        clientAdapter.setOrderBookListener((dto) -> {
            receivedList.add(dto);  // DTO 저장
            System.out.println("Received DTO: " + dto);
            latch.countDown();      // 메시지 수신 후 latch 해제
        });

        clientAdapter.connect();

        // 최대 10초 기다림
        boolean success = latch.await(10, TimeUnit.SECONDS);
        if (!success) {
            System.out.println("No messages received within 10 seconds");
        } else {
            System.out.println("Total messages received: " + receivedList.size());
        }

        clientAdapter.disconnect();
    }

    @Test
    void connectAndReceiveOrderBookSimpleTest() {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        URI uri = URI.create(socketData.getBithumbSocketUri());

        client.execute(uri, session -> {
            // JSON 요청 객체 구성
            List<Map<String, Object>> request = new ArrayList<>();

            Map<String, Object> ticket = new HashMap<>();
            ticket.put("ticket", "test example");
            request.add(ticket);

            Map<String, Object> type = new HashMap<>();
            type.put("type", "orderbook");
            type.put("codes", Arrays.asList("KRW-BCH"));
            type.put("level", 1.0); // Double/int 가능
            type.put("format", "DEFAULT"); // format을 여기 안으로 넣음
            request.add(type);

            ObjectMapper mapper = new ObjectMapper();
            String req;
            try {
                req = mapper.writeValueAsString(request);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println("OrderBook request sent: " + req);

            // 요청 보내고 응답 1개 이상 받을 때까지 출력
            return session.send(Mono.just(session.textMessage(req)))
                    .thenMany(session.receive()
                            .map(msg -> msg.getPayloadAsText())
                            .doOnNext(payload -> {
                                OrderBookBithumbDto orderBookBithumbDto = matchingSocketDataMapper.toOrderBookBithumbDto(payload);
                                System.out.println("orderBookBithumDto: " + orderBookBithumbDto);
                            })
                            .take(2) // 메시지 5개까지만 받고 끊기
                    )
                    .then();
        }).block(Duration.ofSeconds(30)); // 최대 20초 안에 끝내기
    }
}
