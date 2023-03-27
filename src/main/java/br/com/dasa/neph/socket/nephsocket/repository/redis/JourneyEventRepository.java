package br.com.dasa.neph.socket.nephsocket.repository.redis;

import br.com.dasa.neph.socket.nephsocket.models.redis.JourneyEventRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface JourneyEventRepository extends CrudRepository<JourneyEventRedis, UUID> {

    Optional<JourneyEventRedis> findByTicket(UUID ticketSocket);

}
