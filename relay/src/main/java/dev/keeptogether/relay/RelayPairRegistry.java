package dev.keeptogether.relay;

import dev.keeptogether.protocol.relay.v1.RelayHandshake;
import dev.keeptogether.protocol.relay.v1.RelayRole;
import dev.keeptogether.protocol.relay.v1.RelayTicketClaims;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class RelayPairRegistry {

    private final ConcurrentHashMap<UUID, PendingPair> pending = new ConcurrentHashMap<>();
    private final Set<UUID> consumedTickets = ConcurrentHashMap.newKeySet();

    void register(Channel channel, RelayTicketClaims claims) {
        channel.config().setAutoRead(false);
        if (!consumedTickets.add(claims.ticketId())) {
            channel.close();
            return;
        }

        pending.compute(claims.sessionId(), (sessionId, existing) -> {
            var pair = existing == null ? new PendingPair() : existing;
            if (!pair.add(claims.role(), channel)) {
                channel.close();
                return pair;
            }
            if (pair.isComplete()) {
                connect(pair.owner, pair.guest);
                return null;
            }
            channel.closeFuture().addListener(ignored -> pending.remove(sessionId, pair));
            return pair;
        });
    }

    private static void connect(Channel owner, Channel guest) {
        configure(owner, guest);
        configure(guest, owner);
    }

    private static void configure(Channel channel, Channel peer) {
        channel.eventLoop().execute(() -> {
            var pipeline = channel.pipeline();
            if (pipeline.get(RelayHandshakeHandler.class) != null) {
                pipeline.remove(RelayHandshakeHandler.class);
            }
            if (pipeline.get(RelayHandshakeDecoder.class) != null) {
                pipeline.remove(RelayHandshakeDecoder.class);
            }
            if (pipeline.get(ReadTimeoutHandler.class) != null) {
                pipeline.remove(ReadTimeoutHandler.class);
            }
            pipeline.addLast(new RelayForwarder(peer));
            channel.writeAndFlush(Unpooled.wrappedBuffer(new byte[]{RelayHandshake.READY}))
                .addListener(ignored -> {
                    channel.config().setAutoRead(true);
                    channel.read();
                });
        });
    }

    private static final class PendingPair {
        private Channel owner;
        private Channel guest;

        boolean add(RelayRole role, Channel channel) {
            if (role == RelayRole.OWNER && owner == null) {
                owner = channel;
                return true;
            }
            if (role == RelayRole.GUEST && guest == null) {
                guest = channel;
                return true;
            }
            return false;
        }

        boolean isComplete() {
            return owner != null && guest != null;
        }
    }
}
