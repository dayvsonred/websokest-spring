package br.com.dasa.neph.socket.nephsocket.controllers;

import br.com.dasa.neph.socket.nephsocket.dtos.EventDto;
import br.com.dasa.neph.socket.nephsocket.dtos.RegistryDto;
import br.com.dasa.neph.socket.nephsocket.dtos.events.RegistryEventDTO;
import br.com.dasa.neph.socket.nephsocket.services.JourneyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/v1/journey")
@RestController
public class JourneyController {

    private final JourneyService journeyService;

    @PostMapping(value = "/registry", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegistryDto> registryJourney(@RequestBody RegistryEventDTO registryEventDTO){
        return ResponseEntity.ok(journeyService.registryJourneyTicket(registryEventDTO));
    }

    @PostMapping(value = "/event", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> eventJourney(@RequestBody EventDto eventDto){
        journeyService.publisherEvent(eventDto);
        return ResponseEntity.ok().build();
    }
}
