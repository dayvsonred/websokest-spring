package br.com.dasa.neph.socket.nephsocket.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SchedulingMessageInputDto {


    private String msg;
    private String brandId;
    private List<String> patients;
    private String note;

}
