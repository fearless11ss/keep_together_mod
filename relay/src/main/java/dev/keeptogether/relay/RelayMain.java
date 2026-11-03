package dev.keeptogether.relay;

import io.netty.util.Version;

public final class RelayMain {

    private RelayMain() {
    }

    public static void main(String[] args) {
        var configuration = RelayConfiguration.fromEnvironment(System.getenv());
        var nettyVersion = Version.identify().get("netty-common");
        var versionLabel = nettyVersion == null ? "unknown" : nettyVersion.artifactVersion();

        System.getLogger(RelayMain.class.getName()).log(
            System.Logger.Level.INFO,
            "Keep Together Relay scaffold ready for {0}:{1} (Netty {2}). "
                + "No socket is opened until the M1 tunnel spike.",
            configuration.bindHost(),
            configuration.bindPort(),
            versionLabel);
    }
}
