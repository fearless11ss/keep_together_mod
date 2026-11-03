package dev.keeptogether.protocol.relay.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RelayHandshakeTest {

    @Test
    void roundTripsTicket() {
        var ticket = "payload.signature";

        assertThat(RelayHandshake.decode(RelayHandshake.encode(ticket))).isEqualTo(ticket);
    }

    @Test
    void rejectsWrongMagic() {
        var encoded = RelayHandshake.encode("payload.signature");
        encoded[0] = 'X';

        assertThatThrownBy(() -> RelayHandshake.decode(encoded))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Relay handshake is invalid");
    }
}
