# World tunnel

The Tunnel Module hides relay handshakes, local listeners, socket lifetimes, and
bidirectional byte copying behind `SocketWorldTunnel`. It targets Java 17 so the
same implementation can run inside the Forge mod.

M1 deliberately uses plaintext transport on trusted development networks. It
must not be exposed to the public Internet. TLS, inner stream encryption,
reconnect behavior, quotas, and multiple guests are M4 work.
