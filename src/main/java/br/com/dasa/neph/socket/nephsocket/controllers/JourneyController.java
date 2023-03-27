package br.com.dasa.neph.socket.nephsocket.controllers;

import br.com.dasa.neph.socket.nephsocket.dtos.EventDto;
import br.com.dasa.neph.socket.nephsocket.dtos.RegistryDto;
import br.com.dasa.neph.socket.nephsocket.dtos.events.RegistryEventDTO;
import br.com.dasa.neph.socket.nephsocket.services.JourneyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequestMapping("/v1/journey")
@RestController
public class JourneyController {

    private final JourneyService journeyService;


    @PostMapping("/registry")
    public ResponseEntity<RegistryDto> registryJourney(@RequestBody RegistryEventDTO registryEventDTO){
        return ResponseEntity.ok(journeyService.registryJourneyTicket(registryEventDTO));
    }

    @PostMapping("/event")
    public ResponseEntity<EventDto> eventJourney(@RequestBody RegistryEventDTO registryEventDTO){
        journeyService.publisherEvent();
        return ResponseEntity.ok(new EventDto("21323", LocalDateTime.now(), null , null, null));
    }
}
