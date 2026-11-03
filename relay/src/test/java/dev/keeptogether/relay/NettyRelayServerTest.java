package dev.keeptogether.relay;

import static org.assertj.core.api.Assertions.assertThat;

import dev.keeptogether.protocol.relay.v1.HmacRelayTicketCodec;
import dev.keeptogether.protocol.relay.v1.RelayHandshake;
import dev.keeptogether.protocol.relay.v1.RelayRole;
import dev.keeptogether.protocol.relay.v1.RelayTicketClaims;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NettyRelayServerTest {

    private static final Instant NOW = Instant.parse("2026-11-03T06:00:00Z");
    private static final byte[] SECRET =
        "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

    @Test
    void pairsAuthorizedStreamsAndRelaysBytesInBothDirections() throws Exception {
        var codec = new HmacRelayTicketCodec(SECRET, Clock.fixed(NOW, ZoneOffset.UTC));
        try (var relay = new NettyRelayServer(
            new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
            codec)) {
            var address = relay.start();
            var sessionId = UUID.randomUUID();

            try (var owner = connect(address, codec, sessionId, RelayRole.OWNER);
                 var guest = connect(address, codec, sessionId, RelayRole.GUEST)) {
                assertThat(owner.getInputStream().read()).isEqualTo(RelayHandshake.READY);
                assertThat(guest.getInputStream().read()).isEqualTo(RelayHandshake.READY);

                var guestPayload = new byte[]{0, 1, 2, 3, (byte) 255};
                guest.getOutputStream().write(guestPayload);
                assertThat(owner.getInputStream().readNBytes(guestPayload.length))
                    .containsExactly(guestPayload);

                var ownerPayload = "minecraft-stream".getBytes(StandardCharsets.US_ASCII);
                owner.getOutputStream().write(ownerPayload);
                assertThat(guest.getInputStream().readNBytes(ownerPayload.length))
                    .containsExactly(ownerPayload);
            }
        }
    }

    @Test
    void rejectsReplayedTicket() throws Exception {
        var codec = new HmacRelayTicketCodec(SECRET, Clock.fixed(NOW, ZoneOffset.UTC));
        try (var relay = new NettyRelayServer(
            new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
            codec)) {
            var address = relay.start();
            var sessionId = UUID.randomUUID();
            var ownerTicket = codec.issue(claims(sessionId, RelayRole.OWNER));
            var guestTicket = codec.issue(claims(sessionId, RelayRole.GUEST));

            try (var owner = connect(address, ownerTicket);
                 var guest = connect(address, guestTicket)) {
                assertThat(owner.getInputStream().read()).isEqualTo(RelayHandshake.READY);
                assertThat(guest.getInputStream().read()).isEqualTo(RelayHandshake.READY);

                try (var replay = connect(address, ownerTicket)) {
                    replay.setSoTimeout(2_000);
                    assertThat(replay.getInputStream().read()).isEqualTo(-1);
                }
                assertThat(owner.isClosed()).isFalse();
                assertThat(guest.isClosed()).isFalse();
            }
        }
    }

    private static Socket connect(
        InetSocketAddress address,
        HmacRelayTicketCodec codec,
        UUID sessionId,
        RelayRole role
    ) throws IOException {
        return connect(address, codec.issue(claims(sessionId, role)));
    }

    private static Socket connect(InetSocketAddress address, String ticket) throws IOException {
        var socket = new Socket();
        socket.setSoTimeout(5_000);
        socket.connect(address, 5_000);
        socket.getOutputStream().write(RelayHandshake.encode(ticket));
        socket.getOutputStream().flush();
        return socket;
    }

    private static RelayTicketClaims claims(UUID sessionId, RelayRole role) {
        return new RelayTicketClaims(
            UUID.randomUUID(),
            sessionId,
            role,
            NOW.plusSeconds(60));
    }
}
