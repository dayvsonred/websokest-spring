package br.com.dasa.neph.socket.nephsocket.handler;

import br.com.dasa.neph.socket.nephsocket.dtos.ChatMessage;
import br.com.dasa.neph.socket.nephsocket.dtos.EventDto;
import br.com.dasa.neph.socket.nephsocket.dtos.events.Event;
import br.com.dasa.neph.socket.nephsocket.dtos.events.EventType;
import br.com.dasa.neph.socket.nephsocket.models.redis.JourneyEventRedis;
import br.com.dasa.neph.socket.nephsocket.repository.redis.JourneyEventRepository;
import br.com.dasa.neph.socket.nephsocket.services.JourneyService;
import br.com.dasa.neph.socket.nephsocket.services.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final TicketService ticketService;
    private final JourneyService journeyService;

    private final Map<String, WebSocketSession> sessions;

    public WebSocketHandler(TicketService ticketService, JourneyService journeyService){
        this.ticketService = ticketService;
        this.journeyService = journeyService;
        sessions = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        try{
            log.info("[afterConnectionEstablished] session id " + session.getId());

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    session.sendMessage(
//                            new TextMessage("Success connect " + UUID.randomUUID())
//                    );
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        },2000L, 2000L);

            Optional<String> ticket = ticketOf(session);
            if (ticket.isEmpty() || ticket.get().isBlank()) {
                log.warn("session " + session.getId() + " without ticket");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            log.warn("Validating with ticket " + ticket);
            Optional<JourneyEventRedis> journeyEventRedis = journeyService.findJourneyByTicketId(UUID.fromString(ticket.get()));
            if (journeyEventRedis.isEmpty()) {
                log.warn("session " + session.getId() + " with invalid ticket");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            sessions.put(ticket.get(), session);
//        userIds.put(session.getId(), userId.get());
//        sendChatUsers(session);
            journeyService.addSessionToTicket(ticket.get(), session.getId());
            log.info("Connection success session "+ session.getId() + " by with ticket " + ticket.get());
        }catch (Exception e){
            log.error("ERROR AFTER CONNECTION");
            throw new RuntimeException(e);
        }
    }

    private void close(WebSocketSession session, CloseStatus status){
        try {
            session.close(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<String> ticketOf(WebSocketSession session){
        return Optional
                .ofNullable(session.getUri())
                .map(UriComponentsBuilder::fromUri)
                .map(UriComponentsBuilder::build)
                .map(UriComponents::getQueryParams)
                .map(it -> it.get("ticket"))
                .flatMap(it -> it.stream().findFirst())
                .map(String::trim);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("[handleTextMessage] message " + message.getPayload());
        if(message.getPayload().equals("ping")){ session.sendMessage(new TextMessage("pong")); return; }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("[afterConnectionClosed] session id " + session.getId());
    }

    public void notify(ChatMessage chatMessage) {
        Event<ChatMessage> event = new Event<>(EventType.CHAT_MESSAGE_WAS_CREATED, chatMessage);
        List<String> userIds = List.of(chatMessage.from().id(), chatMessage.to().id());
        userIds.stream()
                .distinct()
                .map(sessions::get)
                .filter(Objects::nonNull)
                .forEach(session -> sendEvent(session, event));
        log.info("new message was notified");
    }

    public void notifyEvent(EventDto eventDto) {
        try {
            WebSocketSession session = sessions.get(eventDto.ticket());
            session.sendMessage(new TextMessage(eventDto.message()));
            log.info("new message was notified");
        } catch (IOException e) {
            log.error("ERROR notified message by ticket "+ eventDto.ticket() );
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void sendEvent(WebSocketSession session, Event<?> event) {
        try {
            String eventSerialized = new ObjectMapper().writeValueAsString(event);
            session.sendMessage(new TextMessage(eventSerialized));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
