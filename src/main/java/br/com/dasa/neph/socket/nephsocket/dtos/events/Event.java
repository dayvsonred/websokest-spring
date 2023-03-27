package br.com.dasa.neph.socket.nephsocket.dtos.events;

public record Event<T>(EventType type, T payload) {
}
