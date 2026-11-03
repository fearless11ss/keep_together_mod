package dev.keeptogether.mod.m1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;

class M1TunnelConfigurationTest {

    @Test
    void isOffByDefault() {
        var configuration = M1TunnelConfiguration.fromEnvironment(Map.of());

        assertThat(configuration.mode()).isEqualTo(M1TunnelConfiguration.Mode.OFF);
        assertThat(configuration.relayAddress().getHostString()).isEqualTo("127.0.0.1");
        assertThat(configuration.relayAddress().getPort()).isEqualTo(25570);
    }

    @Test
    void requiresTicketWhenEnabled() {
        assertThatThrownBy(() -> M1TunnelConfiguration.fromEnvironment(Map.of(
            "KT_M1_MODE", "owner")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("KT_M1_TICKET is required in owner or guest mode");
    }

    @Test
    void readsGuestConfiguration() {
        var configuration = M1TunnelConfiguration.fromEnvironment(Map.of(
            "KT_M1_MODE", "guest",
            "KT_M1_RELAY_HOST", "relay.example.test",
            "KT_M1_RELAY_PORT", "24444",
            "KT_M1_LOCAL_PORT", "25566",
            "KT_M1_TICKET", "ticket"));

        assertThat(configuration.mode()).isEqualTo(M1TunnelConfiguration.Mode.GUEST);
        assertThat(configuration.relayAddress().getHostString()).isEqualTo("relay.example.test");
        assertThat(configuration.relayAddress().getPort()).isEqualTo(24444);
        assertThat(configuration.guestLocalPort()).isEqualTo(25566);
    }
}
