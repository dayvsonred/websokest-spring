package br.com.dasa.neph.socket.nephsocket.publisher;

import br.com.dasa.neph.socket.nephsocket.dtos.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JourneyEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${api.rabbitmq.journey.event.exchange}")
    private String exchange;

    @Value("${api.rabbitmq.journey.event.routing}")
    private String routing;

    public void send(EventDto eventDto) {
        try {
            rabbitTemplate.convertAndSend(exchange, routing, eventDto);
            log.info("New event send");
        }catch ( RuntimeException e){
            log.info("ERROR send event ticket {}, type {}, message {}", eventDto.ticket(), eventDto.type(), eventDto.message());
            throw new RuntimeException(e);
        }
    }
}
