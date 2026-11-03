package dev.keeptogether.protocol.relay.v1;

public final class InvalidRelayTicketException extends IllegalArgumentException {

    public InvalidRelayTicketException() {
        super("Relay ticket is invalid or expired");
    }
}
