package dev.keeptogether.protocol.relay.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class HmacRelayTicketCodecTest {

    private static final Instant NOW = Instant.parse("2026-11-03T06:00:00Z");
    private static final byte[] SECRET =
        "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
    private final HmacRelayTicketCodec codec = new HmacRelayTicketCodec(
        SECRET,
        Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void roundTripsSignedClaims() {
        var claims = new RelayTicketClaims(
            UUID.randomUUID(),
            UUID.randomUUID(),
            RelayRole.OWNER,
            NOW.plusSeconds(60));

        assertThat(codec.verify(codec.issue(claims))).isEqualTo(claims);
    }

    @Test
    void rejectsExpiredTickets() {
        var claims = new RelayTicketClaims(
            UUID.randomUUID(),
            UUID.randomUUID(),
            RelayRole.GUEST,
            NOW);

        assertThatThrownBy(() -> codec.verify(codec.issue(claims)))
            .isInstanceOf(InvalidRelayTicketException.class)
            .hasMessage("Relay ticket is invalid or expired");
    }

    @Test
    void rejectsTamperingWithoutDisclosingWhy() {
        var claims = new RelayTicketClaims(
            UUID.randomUUID(),
            UUID.randomUUID(),
            RelayRole.GUEST,
            NOW.plusSeconds(60));
        var ticket = codec.issue(claims);
        var replacement = ticket.endsWith("A") ? "B" : "A";
        var tampered = ticket.substring(0, ticket.length() - 1) + replacement;

        assertThatThrownBy(() -> codec.verify(tampered))
            .isInstanceOf(InvalidRelayTicketException.class)
            .hasMessage("Relay ticket is invalid or expired");
    }
}
