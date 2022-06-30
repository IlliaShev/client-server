package ua.shevchyk.clientserver.models;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ResponseMessage implements Message, Serializable {

    private String response;
}
