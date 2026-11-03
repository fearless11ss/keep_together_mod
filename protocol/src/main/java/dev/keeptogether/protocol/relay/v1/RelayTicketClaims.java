package dev.keeptogether.protocol.relay.v1;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record RelayTicketClaims(
    UUID ticketId,
    UUID sessionId,
    RelayRole role,
    Instant expiresAt
) {
    public RelayTicketClaims {
        Objects.requireNonNull(ticketId, "ticketId");
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(role, "role");
        Objects.requireNonNull(expiresAt, "expiresAt");
    }
}
