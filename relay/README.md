# Relay

Relay is a standalone JDK 25 / Netty process that validates short-lived signed
Relay Tickets and pairs TCP streams for a World Session.

Relay has no PostgreSQL access, durable user data, world storage, matchmaking
logic, or plaintext inspection. Its primary constraints are bounded parsing,
connection limits, bandwidth accounting, and failure isolation.

The M1 spike implements one owner/guest pair and opaque byte forwarding. It has
no TLS, inner end-to-end encryption, durable ticket service, or production
limits yet. Bind it to loopback or a trusted development network only. See
[`docs/development/m1-world-tunnel.md`](../docs/development/m1-world-tunnel.md).
