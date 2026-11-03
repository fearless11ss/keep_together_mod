package dev.keeptogether.relay;

import dev.keeptogether.protocol.relay.v1.HmacRelayTicketCodec;
import dev.keeptogether.protocol.relay.v1.RelayRole;
import dev.keeptogether.protocol.relay.v1.RelayTicketClaims;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Local-only ticket issuer for the M1 world-tunnel spike. */
public final class RelayTicketTool {

    private static final int DEFAULT_TTL_MINUTES = 15;
    private static final int MAXIMUM_TTL_MINUTES = 60;

    private RelayTicketTool() {
    }

    public static void main(String[] args) {
        var environment = System.getenv();
        var configuration = RelayConfiguration.fromEnvironment(environment);
        var clock = Clock.systemUTC();
        var codec = new HmacRelayTicketCodec(configuration.ticketSecret(), clock);
        var tickets = issue(codec, clock, ticketTtl(environment));

        System.out.println("KT_M1_SESSION_ID=" + tickets.sessionId());
        System.out.println("KT_M1_TICKET_EXPIRES_AT=" + tickets.expiresAt());
        System.out.println("KT_M1_OWNER_TICKET=" + tickets.ownerTicket());
        System.out.println("KT_M1_GUEST_TICKET=" + tickets.guestTicket());
    }

    static TicketPair issue(HmacRelayTicketCodec codec, Clock clock, Duration ttl) {
        if (ttl.isZero() || ttl.isNegative() || ttl.compareTo(Duration.ofMinutes(MAXIMUM_TTL_MINUTES)) > 0) {
            throw new IllegalArgumentException("M1 ticket TTL must be between 1 second and 60 minutes");
        }
        var sessionId = UUID.randomUUID();
        var expiresAt = clock.instant().plus(ttl);
        var owner = codec.issue(new RelayTicketClaims(
            UUID.randomUUID(), sessionId, RelayRole.OWNER, expiresAt));
        var guest = codec.issue(new RelayTicketClaims(
            UUID.randomUUID(), sessionId, RelayRole.GUEST, expiresAt));
        return new TicketPair(sessionId, expiresAt, owner, guest);
    }

    private static Duration ticketTtl(Map<String, String> environment) {
        var rawMinutes = environment.getOrDefault(
            "KT_M1_TICKET_TTL_MINUTES",
            Integer.toString(DEFAULT_TTL_MINUTES));
        try {
            var minutes = Integer.parseInt(rawMinutes);
            if (minutes < 1 || minutes > MAXIMUM_TTL_MINUTES) {
                throw new IllegalArgumentException();
            }
            return Duration.ofMinutes(minutes);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                "KT_M1_TICKET_TTL_MINUTES must be between 1 and 60",
                exception);
        }
    }

    record TicketPair(
        UUID sessionId,
        Instant expiresAt,
        String ownerTicket,
        String guestTicket
    ) {
    }
}
