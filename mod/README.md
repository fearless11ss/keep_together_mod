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

The M0 build only proves that the Forge deployment unit compiles and packages.
Minecraft screens, rendering hooks, networking Adapters, and product Modules
start as explicit M1 technical spikes.
