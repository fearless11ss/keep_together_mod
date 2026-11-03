package dev.keeptogether.protocol.relay.v1;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public final class RelayHandshake {

    public static final byte[] MAGIC = {'K', 'T', 'R', 1};
    public static final int HEADER_BYTES = MAGIC.length + Short.BYTES;
    public static final int MAX_TICKET_BYTES = 512;
    public static final byte READY = 1;

    private RelayHandshake() {
    }

    public static byte[] encode(String ticket) {
        var ticketBytes = Objects.requireNonNull(ticket, "ticket")
            .getBytes(StandardCharsets.US_ASCII);
        if (ticketBytes.length == 0 || ticketBytes.length > MAX_TICKET_BYTES) {
            throw new IllegalArgumentException("Relay ticket length is invalid");
        }
        return ByteBuffer.allocate(HEADER_BYTES + ticketBytes.length)
            .put(MAGIC)
            .putShort((short) ticketBytes.length)
            .put(ticketBytes)
            .array();
    }

    public static String decode(byte[] handshake) {
        Objects.requireNonNull(handshake, "handshake");
        if (handshake.length < HEADER_BYTES) {
            throw new IllegalArgumentException("Relay handshake is incomplete");
        }
        var buffer = ByteBuffer.wrap(handshake);
        var magic = new byte[MAGIC.length];
        buffer.get(magic);
        var ticketLength = Short.toUnsignedInt(buffer.getShort());
        if (!Arrays.equals(magic, MAGIC)
            || ticketLength == 0
            || ticketLength > MAX_TICKET_BYTES
            || buffer.remaining() != ticketLength) {
            throw new IllegalArgumentException("Relay handshake is invalid");
        }
        var ticketBytes = new byte[ticketLength];
        buffer.get(ticketBytes);
        return new String(ticketBytes, StandardCharsets.US_ASCII);
    }
}
