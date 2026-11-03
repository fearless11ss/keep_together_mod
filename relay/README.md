# Relay

Relay is a standalone JDK 25 / Netty process that validates short-lived signed
Relay Tickets and pairs encrypted TCP streams for a World Session.

Relay has no PostgreSQL access, durable user data, world storage, matchmaking
logic, or plaintext inspection. Its primary constraints are bounded parsing,
connection limits, bandwidth accounting, and failure isolation.
