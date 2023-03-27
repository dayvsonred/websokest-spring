package br.com.dasa.neph.socket.nephsocket.dtos;

import java.time.LocalDateTime;

public record RegistryDto (String ticket, LocalDateTime time) {
}
