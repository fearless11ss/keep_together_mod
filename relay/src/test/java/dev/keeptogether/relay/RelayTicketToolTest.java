package dev.keeptogether.relay;

import static org.assertj.core.api.Assertions.assertThat;

import dev.keeptogether.protocol.relay.v1.HmacRelayTicketCodec;
import dev.keeptogether.protocol.relay.v1.RelayRole;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class RelayTicketToolTest {

    @Test
    void issuesOneOwnerAndGuestForTheSameSession() {
        var clock = Clock.fixed(Instant.parse("2026-11-03T12:00:00Z"), ZoneOffset.UTC);
        var codec = new HmacRelayTicketCodec(new byte[32], clock);

        var pair = RelayTicketTool.issue(codec, clock, Duration.ofMinutes(15));

        var owner = codec.verify(pair.ownerTicket());
        var guest = codec.verify(pair.guestTicket());
        assertThat(owner.sessionId()).isEqualTo(pair.sessionId());
        assertThat(guest.sessionId()).isEqualTo(pair.sessionId());
        assertThat(owner.ticketId()).isNotEqualTo(guest.ticketId());
        assertThat(owner.role()).isEqualTo(RelayRole.OWNER);
        assertThat(guest.role()).isEqualTo(RelayRole.GUEST);
        assertThat(pair.expiresAt()).isEqualTo(Instant.parse("2026-11-03T12:15:00Z"));
    }
}
