package br.com.dasa.neph.socket.nephsocket.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class journeyEventConsumer {

    @RabbitListener(queues = {"${api.rabbitmq.journey.event.queue}"})
    public void receiveCreatedAccountEvent(String userSessionRedisDTO) {
        try {

            System.out.println("**********************************************************");
            System.out.println("**********************************************************");
            System.out.println("RabbitListener +++++++++");
            System.out.println("**********************************************************");

            log.info(userSessionRedisDTO);

        } catch (Exception be) {
            log.error(be.getMessage(), be);
        }
    }
}
