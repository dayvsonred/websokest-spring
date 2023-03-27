package br.com.dasa.neph.socket.nephsocket.models.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RedisHash("JourneyEventRedis")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JourneyEventRedis {

    @Id
    private String id;
    private Long idHospital;
    private String patientName;
    private UUID ticket;
    private String patientPhone;
    private Long codTriage;
    private Long codService;


    @TimeToLive(unit = TimeUnit.HOURS)
    private Long ttl;
}
