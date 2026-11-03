package dev.keeptogether.mod.m1;

import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

record M1TunnelConfiguration(
    Mode mode,
    InetSocketAddress relayAddress,
    String ticket,
    int guestLocalPort
) {
    private static final int DEFAULT_RELAY_PORT = 25570;

    M1TunnelConfiguration {
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(relayAddress, "relayAddress");
        if (guestLocalPort < 0 || guestLocalPort > 65_535) {
            throw new IllegalArgumentException("guestLocalPort must be between 0 and 65535");
        }
        if (mode != Mode.OFF && (ticket == null || ticket.isBlank())) {
            throw new IllegalArgumentException("KT_M1_TICKET is required in owner or guest mode");
        }
    }

    static M1TunnelConfiguration fromEnvironment(Map<String, String> environment) {
        var rawMode = environment.getOrDefault("KT_M1_MODE", "off");
        final Mode mode;
        try {
            mode = Mode.valueOf(rawMode.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("KT_M1_MODE must be off, owner, or guest", exception);
        }
        var relayHost = environment.getOrDefault("KT_M1_RELAY_HOST", "127.0.0.1");
        var relayPort = parsePort(
            environment.getOrDefault("KT_M1_RELAY_PORT", Integer.toString(DEFAULT_RELAY_PORT)),
            false,
            "KT_M1_RELAY_PORT");
        var localPort = parsePort(
            environment.getOrDefault("KT_M1_LOCAL_PORT", "0"),
            true,
            "KT_M1_LOCAL_PORT");
        return new M1TunnelConfiguration(
            mode,
            new InetSocketAddress(relayHost, relayPort),
            environment.get("KT_M1_TICKET"),
            localPort);
    }

    private static int parsePort(String value, boolean allowZero, String name) {
        try {
            var port = Integer.parseInt(value);
            var minimum = allowZero ? 0 : 1;
            if (port < minimum || port > 65_535) {
                throw new IllegalArgumentException();
            }
            return port;
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(name + " is not a valid port", exception);
        }
    }

    enum Mode {
        OFF,
        OWNER,
        GUEST
    }
}
