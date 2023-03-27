package br.com.dasa.neph.socket.nephsocket.dtos.events;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistryEventDTO implements Serializable {

    private Long idHospital;
    private String patientName;
    private UUID ticket;
    private String patientPhone;
    private Long codTriage;
    private Long codService;
}
