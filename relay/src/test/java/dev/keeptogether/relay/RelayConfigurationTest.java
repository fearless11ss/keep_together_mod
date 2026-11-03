package dev.keeptogether.relay;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.Base64;
import org.junit.jupiter.api.Test;

class RelayConfigurationTest {

    @Test
    void usesSafeLocalDefaults() {
        var secret = new byte[32];
        var configuration = RelayConfiguration.fromEnvironment(Map.of(
            "KT_RELAY_TICKET_SECRET", Base64.getEncoder().encodeToString(secret)));

        assertThat(configuration.bindHost()).isEqualTo("127.0.0.1");
        assertThat(configuration.bindPort()).isEqualTo(25570);
        assertThat(configuration.ticketSecret()).containsExactly(secret);
    }

    @Test
    void rejectsInvalidPort() {
        assertThatThrownBy(() -> new RelayConfiguration("127.0.0.1", 0, new byte[32]))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("between 1 and 65535");
    }

    @Test
    void requiresTicketSecret() {
        assertThatThrownBy(() -> RelayConfiguration.fromEnvironment(Map.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("KT_RELAY_TICKET_SECRET is required");
    }
}
