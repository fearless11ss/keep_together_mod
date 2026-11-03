package dev.keeptogether.tunnel;

import static org.assertj.core.api.Assertions.assertThat;

import dev.keeptogether.protocol.relay.v1.HmacRelayTicketCodec;
import dev.keeptogether.protocol.relay.v1.RelayRole;
import dev.keeptogether.protocol.relay.v1.RelayTicketClaims;
import dev.keeptogether.relay.NettyRelayServer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class SocketWorldTunnelTest {

    private static final Instant NOW = Instant.parse("2026-11-03T06:00:00Z");
    private static final byte[] SECRET =
        "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

    @Test
    void guestReachesOwnerTargetThroughRelay() throws Exception {
        var codec = new HmacRelayTicketCodec(SECRET, Clock.fixed(NOW, ZoneOffset.UTC));
        var sessionId = UUID.randomUUID();
        try (var relay = new NettyRelayServer(
                 new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), codec);
             var targetListener = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            var relayAddress = relay.start();
            try (var owner = SocketWorldTunnel.openOwner(
                     relayAddress,
                     ticket(codec, sessionId, RelayRole.OWNER),
                     (InetSocketAddress) targetListener.getLocalSocketAddress());
                 var guest = SocketWorldTunnel.openGuest(
                     relayAddress,
                     ticket(codec, sessionId, RelayRole.GUEST),
                     new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
                 var guestClient = new Socket()) {
                guestClient.setSoTimeout(5_000);
                guestClient.connect(guest.localAddress().orElseThrow(), 5_000);
                try (var target = targetListener.accept()) {
                    owner.ready().get(5, TimeUnit.SECONDS);
                    guest.ready().get(5, TimeUnit.SECONDS);

                    var request = "minecraft-login".getBytes(StandardCharsets.US_ASCII);
                    guestClient.getOutputStream().write(request);
                    assertThat(target.getInputStream().readNBytes(request.length))
                        .containsExactly(request);

                    var response = "server-status".getBytes(StandardCharsets.US_ASCII);
                    target.getOutputStream().write(response);
                    assertThat(guestClient.getInputStream().readNBytes(response.length))
                        .containsExactly(response);
                }
            }
        }
    }

    private static String ticket(
        HmacRelayTicketCodec codec,
        UUID sessionId,
        RelayRole role
    ) {
        return codec.issue(new RelayTicketClaims(
            UUID.randomUUID(),
            sessionId,
            role,
            NOW.plusSeconds(60)));
    }
}
