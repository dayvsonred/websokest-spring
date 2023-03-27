package br.com.dasa.neph.socket.nephsocket.dtos;

public record ChatMessage(User from, User to, String text) {
}
