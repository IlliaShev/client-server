package ua.clientserver.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacketPayload {
    private int cType;
    private int bUserId;
    private byte[] payload;
}
