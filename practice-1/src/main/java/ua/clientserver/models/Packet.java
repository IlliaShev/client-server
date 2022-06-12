package ua.clientserver.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Packet {

    private byte bSrc;
    private long bPktId;
    private int wLen;
    private int wCrc16;
    private PacketPayload packetPayload;
    private int wCrc16_end;

}
