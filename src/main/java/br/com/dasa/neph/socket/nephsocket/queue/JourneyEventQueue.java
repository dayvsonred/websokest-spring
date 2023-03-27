package br.com.dasa.neph.socket.nephsocket.queue;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static br.com.dasa.neph.socket.nephsocket.config.rabbit.RabbitListenerConfiguration.*;

@Configuration
public class JourneyEventQueue {

    @Value("${api.rabbitmq.journey.event.exchange}")
    private String nameExchange;

    @Value("${api.rabbitmq.journey.event.queue}")
    private String nameQueue;

    @Value("${api.rabbitmq.journey.event.routing}")
    private String nameRouting;

    @Value("${api.rabbitmq.journey.event.dlq.queue}")
    private String nameDlqQueue;

    @Value("${api.rabbitmq.journey.event.dlq.routing}")
    private String nameDlqRouting;

    @Value("${api.rabbitmq.journey.event.dlq.delay}")
    private Long timeDlqDelay;

    @Value("${api.rabbitmq.journey.event.pkl.queue}")
    private String namePklQueue;

    @Value("${api.rabbitmq.journey.event.pkl.routing}")
    private String namePklRouting;




    @Bean
    public TopicExchange journeyExchange() {
        return new TopicExchange(nameExchange, true, false);
    }

    @Bean
    public Queue journeyQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put(DEAD_LETTER_EXCHANGE_HEADER, nameExchange);
        args.put(DEAD_LETTER_ROUTING_KEY_HEADER, nameDlqRouting);
        return new Queue(nameQueue, true, false, false, args);
    }

    @Bean
    public Binding journeyBinding() {
        return BindingBuilder.bind(journeyQueue()).to(journeyExchange()).with(nameRouting);
    }

    @Bean
    public Queue journeyQueueDLQ() {
        Map<String, Object> args = new HashMap<>();
        args.put(DEAD_LETTER_EXCHANGE_HEADER, nameExchange);
        args.put(DEAD_LETTER_ROUTING_KEY_HEADER, nameRouting);
        args.put(MESSAGE_TTL_HEADER, timeDlqDelay);
        return new Queue(nameDlqQueue, true, false, false, args);
    }

    @Bean
    public Binding journeyBindingDLQ() {
        return BindingBuilder.bind(journeyQueueDLQ()).to(journeyExchange()).with(nameDlqRouting);
    }

    @Bean
    public Queue journeyQueueParkingLot() {
        return new Queue(namePklQueue, true, false, false);
    }

    @Bean
    public Binding journeyBindingParkingLot() {
        return BindingBuilder.bind(journeyQueueParkingLot()).to(journeyExchange()).with(namePklRouting);
    }
}
