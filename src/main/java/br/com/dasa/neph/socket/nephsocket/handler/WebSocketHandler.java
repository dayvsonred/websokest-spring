package br.com.dasa.neph.socket.nephsocket.handler;

import br.com.dasa.neph.socket.nephsocket.dtos.ChatMessage;
import br.com.dasa.neph.socket.nephsocket.dtos.events.Event;
import br.com.dasa.neph.socket.nephsocket.dtos.events.EventType;
import br.com.dasa.neph.socket.nephsocket.repository.redis.JourneyEventRepository;
import br.com.dasa.neph.socket.nephsocket.services.JourneyService;
import br.com.dasa.neph.socket.nephsocket.services.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.logging.Logger;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final static Logger LOGGER = Logger.getLogger(WebSocketHandler.class.getName());

    private final TicketService ticketService;
    private final JourneyService journeyService;

    private final Map<String, WebSocketSession> sessions;

    public WebSocketHandler(TicketService ticketService, JourneyService journeyService){
        this.ticketService = ticketService;
        this.journeyService = journeyService;
        sessions = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LOGGER.info("[afterConnectionEstablished] session id " + session.getId());



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
            LOGGER.warning("session " + session.getId() + " without ticket");
//            session.close(CloseStatus.POLICY_VIOLATION);
//            return;
        }

        LOGGER.warning("Connection with ticket " + ticket.get());
        journeyService.findJourneyByTicketId(UUID.fromString(ticket.get()));
//        session.sendMessage(new TextMessage("Success connect with ticket"+ ticket.get()));
//        Optional<String> userId = ticketService.getUserIdByTicket(ticket.get());
//        if (userId.isEmpty()) {
//            LOGGER.warning("session " + session.getId() + " with invalid ticket");
//            session.close(CloseStatus.POLICY_VIOLATION);
//            return;
//        }
//        sessions.put(userId.get(), session);
//        userIds.put(session.getId(), userId.get());
//        LOGGER.info("session " + session.getId() + " was bind to user " + userId.get());
//        sendChatUsers(session);
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
        LOGGER.info("[handleTextMessage] message " + message.getPayload());
        if(message.getPayload().equals("ping")){ session.sendMessage(new TextMessage("pong")); return; }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        LOGGER.info("[afterConnectionClosed] session id " + session.getId());
    }

    public void notify(ChatMessage chatMessage) {
        Event<ChatMessage> event = new Event<>(EventType.CHAT_MESSAGE_WAS_CREATED, chatMessage);
        List<String> userIds = List.of(chatMessage.from().id(), chatMessage.to().id());
        userIds.stream()
                .distinct()
                .map(sessions::get)
                .filter(Objects::nonNull)
                .forEach(session -> sendEvent(session, event));
        LOGGER.info("new message was notified");
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
