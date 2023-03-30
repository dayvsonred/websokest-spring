package br.com.dasa.neph.socket.nephsocket.consumer;

import br.com.dasa.neph.socket.nephsocket.dtos.EventDto;
import br.com.dasa.neph.socket.nephsocket.handler.WebSocketHandler;
import br.com.dasa.neph.socket.nephsocket.services.JourneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class journeyEventConsumer {

    @Autowired
    private WebSocketHandler webSocketHandler;
    private final JourneyService journeyService;

    @RabbitListener(queues = {"${api.rabbitmq.journey.event.queue}"})
    public void receiveCreatedAccountEvent(EventDto eventDto) {
        try {
            System.out.println("**********************************************************");
            System.out.println("**********************************************************");

            log.info(eventDto.ticket());
            log.info(eventDto.type());
            log.info(eventDto.message().toString());

            webSocketHandler.notifyEvent(eventDto);

            System.out.println("RabbitListener +++++++++");
            System.out.println("**********************************************************");
        } catch (Exception be) {
            log.error("ERROR Critical receive message");
            log.error(be.getMessage(), be);
        }
    }
}
