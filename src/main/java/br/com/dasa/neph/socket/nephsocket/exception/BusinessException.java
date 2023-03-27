package br.com.dasa.neph.socket.nephsocket.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BusinessException extends RuntimeException {

	BusinessException(String message) {
		super(message);
	}

}
