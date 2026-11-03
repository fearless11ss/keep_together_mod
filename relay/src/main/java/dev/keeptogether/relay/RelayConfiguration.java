package dev.keeptogether.relay;

import java.util.Base64;
import java.util.Map;

public record RelayConfiguration(String bindHost, int bindPort, byte[] ticketSecret) {

    private static final int DEFAULT_PORT = 25570;

    public RelayConfiguration {
        if (bindHost == null || bindHost.isBlank()) {
            throw new IllegalArgumentException("bindHost must not be blank");
        }
        if (bindPort < 1 || bindPort > 65_535) {
            throw new IllegalArgumentException("bindPort must be between 1 and 65535");
        }
        if (ticketSecret == null || ticketSecret.length < 32) {
            throw new IllegalArgumentException("ticketSecret must contain at least 32 bytes");
        }
        ticketSecret = ticketSecret.clone();
    }

    @Override
    public byte[] ticketSecret() {
        return ticketSecret.clone();
    }

    public static RelayConfiguration fromEnvironment(Map<String, String> environment) {
        var host = environment.getOrDefault("KT_RELAY_BIND_HOST", "127.0.0.1");
        var port = Integer.parseInt(
            environment.getOrDefault("KT_RELAY_BIND_PORT", Integer.toString(DEFAULT_PORT)));
        var encodedSecret = environment.get("KT_RELAY_TICKET_SECRET");
        if (encodedSecret == null || encodedSecret.isBlank()) {
            throw new IllegalArgumentException("KT_RELAY_TICKET_SECRET is required");
        }
        try {
            return new RelayConfiguration(host, port, Base64.getDecoder().decode(encodedSecret));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                "KT_RELAY_TICKET_SECRET must be Base64 for at least 32 bytes",
                exception);
        }
    }
}
