package dev.keeptogether.mod.m1;

import com.mojang.logging.LogUtils;
import dev.keeptogether.tunnel.SocketWorldTunnel;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.util.HttpUtil;
import org.slf4j.Logger;

final class M1TunnelController {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final M1TunnelConfiguration CONFIGURATION =
        M1TunnelConfiguration.fromEnvironment(System.getenv());
    private static final AtomicReference<SocketWorldTunnel> OWNER = new AtomicReference<>();
    private static final AtomicReference<SocketWorldTunnel> GUEST = new AtomicReference<>();

    private M1TunnelController() {
    }

    static void initializeGuest() {
        if (CONFIGURATION.mode() != M1TunnelConfiguration.Mode.GUEST || GUEST.get() != null) {
            return;
        }
        try {
            var tunnel = SocketWorldTunnel.openGuest(
                CONFIGURATION.relayAddress(),
                CONFIGURATION.ticket(),
                new InetSocketAddress(
                    InetAddress.getLoopbackAddress(),
                    CONFIGURATION.guestLocalPort()));
            if (!GUEST.compareAndSet(null, tunnel)) {
                tunnel.close();
                return;
            }
            var localAddress = tunnel.localAddress().orElseThrow();
            LOGGER.info(
                "Keep Together M1 guest tunnel listening at {}:{}; connect through Multiplayer.",
                localAddress.getHostString(),
                localAddress.getPort());
            logReady("guest", tunnel);
        } catch (IOException exception) {
            LOGGER.error("Keep Together M1 guest tunnel could not start.", exception);
        }
    }

    static void openOwner(IntegratedServer server) {
        if (CONFIGURATION.mode() != M1TunnelConfiguration.Mode.OWNER || OWNER.get() != null) {
            return;
        }

        var port = server.getPort();
        if (!server.isPublished()) {
            port = HttpUtil.getAvailablePort();
            if (!server.publishServer(server.getDefaultGameType(), false, port)) {
                LOGGER.error("Keep Together M1 could not publish the IntegratedServer locally.");
                return;
            }
        }

        var tunnel = SocketWorldTunnel.openOwner(
            CONFIGURATION.relayAddress(),
            CONFIGURATION.ticket(),
            new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
        if (!OWNER.compareAndSet(null, tunnel)) {
            tunnel.close();
            return;
        }
        LOGGER.info("Keep Together M1 owner tunnel waiting for its paired guest.");
        logReady("owner", tunnel);
    }

    static void closeOwner() {
        close(OWNER);
    }

    static void closeAll() {
        close(OWNER);
        close(GUEST);
    }

    private static void logReady(String role, SocketWorldTunnel tunnel) {
        tunnel.ready().whenComplete((ignored, failure) -> {
            if (failure == null) {
                LOGGER.info("Keep Together M1 {} tunnel is connected.", role);
            } else {
                LOGGER.error("Keep Together M1 {} tunnel failed.", role, failure);
            }
        });
    }

    private static void close(AtomicReference<SocketWorldTunnel> reference) {
        var tunnel = reference.getAndSet(null);
        if (tunnel != null) {
            tunnel.close();
        }
    }
}
