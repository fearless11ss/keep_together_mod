# Forge mod

The mod is the Minecraft-facing deployment unit for Java 17 and Forge 1.20.1.
It will contain Minecraft UI, IntegratedServer, rendering, and networking
Adapters around client Modules defined in `ARCHITECTURE.md`.

It must not:

- contain Backend persistence or moderation rules;
- authorize by Minecraft nickname;
- call Bouncy Castle or Tink outside CryptoEngine;
- download or execute arbitrary code;
- distribute Minecraft binaries or a launcher.

The M1 world-tunnel spike publishes an IntegratedServer and connects it to the
development Relay through the World Tunnel Module. Two isolated ForgeGradle
runs are available as `runOwnerClient` and `runGuestClient`. This is a
development path, not production authorization or transport security; follow
[`docs/development/m1-world-tunnel.md`](../docs/development/m1-world-tunnel.md).

The distributable `*-all.jar` embeds the Java 17 `tunnel` and `protocol`
libraries through Forge Jar-in-Jar. Use that artifact for manual installation;
the plain jar is an intermediate build output.
