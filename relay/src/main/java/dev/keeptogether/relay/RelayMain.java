package dev.keeptogether.relay;

import dev.keeptogether.protocol.relay.v1.HmacRelayTicketCodec;
import io.netty.util.Version;
import java.net.InetSocketAddress;
import java.time.Clock;

public final class RelayMain {

    private RelayMain() {
    }

    public static void main(String[] args) throws InterruptedException {
        var configuration = RelayConfiguration.fromEnvironment(System.getenv());
        var nettyVersion = Version.identify().get("netty-common");
        var versionLabel = nettyVersion == null ? "unknown" : nettyVersion.artifactVersion();
        var ticketCodec = new HmacRelayTicketCodec(configuration.ticketSecret(), Clock.systemUTC());
        var server = new NettyRelayServer(
            new InetSocketAddress(configuration.bindHost(), configuration.bindPort()),
            ticketCodec);
        var boundAddress = server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close, "relay-shutdown"));

        System.getLogger(RelayMain.class.getName()).log(
            System.Logger.Level.INFO,
            "Keep Together M1 Relay listening on {0}:{1} (Netty {2}). "
                + "This spike has no TLS and must remain on a trusted network.",
            boundAddress.getHostString(),
            boundAddress.getPort(),
            versionLabel);
        Thread.currentThread().join();
    }
}
