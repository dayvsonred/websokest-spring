package br.com.dasa.neph.socket.nephsocket.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JourneyEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${api.rabbitmq.journey.event.exchange}")
    private String exchange;

    @Value("${api.rabbitmq.journey.event.routing}")
    private String routing;

    public void send(String userSessionRedisDTO) {
        System.out.println("BEFORE ------------- Send msg = " );
        System.out.println(exchange);
        System.out.println(routing);
        rabbitTemplate.convertAndSend(exchange, routing, userSessionRedisDTO);
        System.out.println("Send msg = " + userSessionRedisDTO);
    }
}
