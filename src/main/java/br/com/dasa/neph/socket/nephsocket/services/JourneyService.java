package br.com.dasa.neph.socket.nephsocket.services;

import br.com.dasa.neph.socket.nephsocket.dtos.EventDto;
import br.com.dasa.neph.socket.nephsocket.dtos.RegistryDto;
import br.com.dasa.neph.socket.nephsocket.dtos.events.RegistryEventDTO;
import br.com.dasa.neph.socket.nephsocket.handler.WebSocketHandler;
import br.com.dasa.neph.socket.nephsocket.models.redis.JourneyEventRedis;
import br.com.dasa.neph.socket.nephsocket.publisher.JourneyEventPublisher;
import br.com.dasa.neph.socket.nephsocket.repository.redis.JourneyEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@Slf4j
@RequiredArgsConstructor
public class JourneyService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final JourneyEventRepository journeyEventRepository;
    private final JourneyEventPublisher journeyEventPublisher;

    @Value("${app.redis.expire.journey-ticket}")
    private Long TTL_TICKET_EXPIRATION;

    // A ideia e esse seciço ser chamado por uma subicript em uma fila do rabbit ou krafika
    // vai receber ante de enciar para tuilio o link
    // para ja registra os dado do pacie3nte e telefone e ser usado para arualizar o sokets
    public RegistryDto registryJourneyTicket(RegistryEventDTO registryEventDTO){
        try {
            return new RegistryDto(this.journeyEventRepository.save(JourneyEventRedis.builder()
                            .id(registryEventDTO.getTicket().toString())
                    .codService(registryEventDTO.getCodService())
                    .codTriage(registryEventDTO.getCodTriage())
                    .ticket(registryEventDTO.getTicket())
                    .idHospital(registryEventDTO.getIdHospital())
                    .patientName(registryEventDTO.getPatientName())
                    .patientPhone(registryEventDTO.getPatientPhone())
                    .ttl(TTL_TICKET_EXPIRATION)
                    .build()).getId(), LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));

        }catch ( Exception e ){
            log.info("ERROR CRITICAL Registry in redis ticket - {}", registryEventDTO.getTicket());
            throw new RuntimeException(e);
        }
    }

    public void addSessionToTicket(String ticket, String sessionId){
        try {
            JourneyEventRedis journeyEventRedis = this.getJourneyByTicketId(UUID.fromString(ticket));
            journeyEventRedis.setSessionId(sessionId);
            this.journeyEventRepository.save(journeyEventRedis);
        }catch ( Exception e ){
            log.info("ERROR CRITICAL Registry in redis ticket - {}", ticket);
            throw new RuntimeException(e);
        }
    }

    public Optional<JourneyEventRedis> findJourneyByTicketId(UUID id){
        try {
            return Optional.ofNullable(this.journeyEventRepository.findById(id).orElse(null));
        }catch ( Exception e ){
            log.info("ERROR Find ID Ticket in redis ticket - {}", id.toString());
            throw new RuntimeException(e);
        }
    }

    public JourneyEventRedis getJourneyByTicketId(UUID id){
        try {
            JourneyEventRedis journeyEventRedis = this.journeyEventRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("ERROR Not found Journey TicketId " + id )
            );
            log.info("Find id Ticket success User - {}", journeyEventRedis.getPatientName());

            return journeyEventRedis;
        }catch ( Exception e ){
            log.info("ERROR Find ID Ticket in redis ticket - {}", id.toString());
            throw new RuntimeException(e);
        }
    }

    public void publisherEvent(EventDto eventDto){
        try {
            journeyEventPublisher.send(eventDto);
        }catch ( Exception e ){
            log.info("ERROR Publisher event in - {}", LocalDateTime.now().toString());
            throw new RuntimeException(e);
        }
    }

    public void sendEventMsgToClient(EventDto eventDto){
        try {
            journeyEventPublisher.send(eventDto);
        }catch ( Exception e ){
            log.info("ERROR Publisher event in - {}", LocalDateTime.now().toString());
            throw new RuntimeException(e);
        }
    }

}
