package dev.keeptogether.relay;

import java.util.Map;

public record RelayConfiguration(String bindHost, int bindPort) {

    private static final int DEFAULT_PORT = 25570;

    public RelayConfiguration {
        if (bindHost == null || bindHost.isBlank()) {
            throw new IllegalArgumentException("bindHost must not be blank");
        }
        if (bindPort < 1 || bindPort > 65_535) {
            throw new IllegalArgumentException("bindPort must be between 1 and 65535");
        }
    }

    public static RelayConfiguration fromEnvironment(Map<String, String> environment) {
        var host = environment.getOrDefault("KT_RELAY_BIND_HOST", "127.0.0.1");
        var port = Integer.parseInt(
            environment.getOrDefault("KT_RELAY_BIND_PORT", Integer.toString(DEFAULT_PORT)));
        return new RelayConfiguration(host, port);
    }
}
