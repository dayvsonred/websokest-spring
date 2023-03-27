package br.com.dasa.neph.socket.nephsocket.dtos;

import java.time.LocalDateTime;

public record EventDto (String ticket, LocalDateTime time, String type, String payload, String message )  {
}
