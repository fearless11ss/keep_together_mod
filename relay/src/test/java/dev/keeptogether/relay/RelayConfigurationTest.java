package dev.keeptogether.relay;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;

class RelayConfigurationTest {

    @Test
    void usesSafeLocalDefaults() {
        var configuration = RelayConfiguration.fromEnvironment(Map.of());

        assertThat(configuration.bindHost()).isEqualTo("127.0.0.1");
        assertThat(configuration.bindPort()).isEqualTo(25570);
    }

    @Test
    void rejectsInvalidPort() {
        assertThatThrownBy(() -> new RelayConfiguration("127.0.0.1", 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("between 1 and 65535");
    }
}
