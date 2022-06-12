package ua.clientserver.models;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record Message(int priority, String author, String text) implements Serializable {
}
