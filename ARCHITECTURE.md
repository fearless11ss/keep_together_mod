# Architecture

## Design principles

Keep Together favors deep **Modules**: substantial behavior behind small,
stable **Interfaces**. Each Interface is the test surface for its Module.
Infrastructure and framework details enter through **Adapters** at deliberate
**Seams**.

Rules:

1. A Module owns its invariants and data changes.
2. A caller does not query another Module's tables.
3. An Interface includes behavior, invariants, ordering, errors, and performance
   expectations—not only Java method shapes.
4. A Seam exists only when at least two Adapters are justified, normally a
   production Adapter and a test Adapter.
5. Tests assert observable behavior through Interfaces rather than internal
   classes or tables.
6. Framework types do not cross core Module Interfaces.
7. The mod, Backend, Relay, and Website are deployment units, not domain
   Modules.

## Deployment shape

```text
                       +--------------------+
                       |   Static Website   |
                       +----------+---------+
                                  |
                              HTTPS/JSON
                                  |
+------------------+    +---------v----------+      +------------------+
| Forge Mod        |<-->| Backend            |----->| Russian object   |
| Java 17          | WS | Modular monolith   |      | storage          |
|                  |    | JDK 25             |      +------------------+
| CryptoEngine     |    +---------+----------+
| Local cache      |              |
| World tunnel     |          PostgreSQL
+--------+---------+              |
         |                  +-----v------+
   encrypted TCP            | Russian DB |
         |                  +------------+
+--------v---------+
| Relay            |
| JDK 25 / Netty   |
| no durable data  |
+------------------+
```

The first production topology uses separate Russian Data and Relay servers.
Images and backups use separate Russian storage. A foreign processor or relay
region is forbidden until the cross-border legal gate is resolved.

## Repository build roots

- `mod/`: ForgeGradle, Java 17.
- `backend/` and `relay/`: server Gradle build, JDK 25 runtime.
- `protocol/`: Protobuf schemas; generated Java targets Java 17 and generated
  TypeScript targets the Website toolchain.
- `crypto/`: Java 17-compatible cryptographic Module.
- `web/`: React, TypeScript, Vite, Node 24 LTS, pnpm.
- `test-support/`: in-memory Adapters, fixtures, and multi-process harnesses.
- `infra/`: Docker Compose and environment configuration.

The build roots are coordinated by CI. They do not share one Gradle wrapper,
because ForgeGradle and the server build have different runtime constraints.

## Backend Modules

### Identity

Owns Accounts, Devices, credentials, passkeys, TOTP, phone verification,
login sessions, age confirmation, and legal acceptance records.

External Interface outcomes include registering an Account, authorizing a
Device, verifying a phone, rotating sessions, revoking a Device, and beginning
account deletion. The Interface never exposes password hashes, phone plaintext,
or raw refresh tokens.

### Social Graph

Owns Friend Requests, Friendships, and Blocks. It enforces request expiration,
limits, mutuality, and the fact that Blocks override every social permission.

### Profiles

Owns Display Names, profile text, privacy settings, playtime totals, and the
privacy-filtered Profile projection. It references Accounts by Account ID and
does not own login credentials or Friendships.

### Presence

Owns ephemeral availability and world-status observations. It derives
visibility from Profiles and Social Graph decisions but does not persist a
historical activity trail.

### World Sessions

Owns World Sessions, Access Grants, World Invitations, capacity, and Relay
Tickets. It never owns Minecraft world data. Signed Relay Tickets are the only
authorization understood by Relay.

### Messaging Delivery

Owns Conversation membership metadata, ordered encrypted envelopes, delivery
state, quotas, and attachment references. It cannot decrypt Message Events or
Attachments. MLS state and plaintext live on registered Devices.

### Skin Library

Owns validated skins, the private library, active-skin selection, and fallback
rules. It stores only sanitized Minecraft-compatible PNG output.

### Moderation

Owns Reports, selected disclosed evidence, Sanctions, appeals, and the
administrative audit trail. It can restrict capabilities through other Module
Interfaces but cannot bypass E2E to fetch arbitrary conversations.

## Client Modules

### Account Session

Presents a small Interface for device authorization, token rotation, revocation,
and emergency account protection. HTTP and secure-local-storage details remain
inside its implementation.

### CryptoEngine

Presents operations for device identity, conversation membership, Message
Event encryption/decryption, key updates, recovery, safety numbers, and
Attachment keys. Bouncy Castle MLS and Tink are implementation details.

No Forge, Spring, UI, or network code calls Bouncy Castle or Tink directly.

### Messaging

Presents conversation timelines and commands to create Message Events. It
combines CryptoEngine, encrypted local cache, and Backend delivery behind one
Interface. Callers do not coordinate MLS ordering themselves.

### World Tunnel

Presents open, join, revoke, and close operations for World Sessions. Forge
IntegratedServer access, Relay framing, and reconnect behavior stay inside the
implementation.

### Identity Rendering

Presents privacy-filtered names, skin textures, profile cards, and non-member
markers without modifying Minecraft GameProfile identity.

## Seams and Adapters

| Seam | Production Adapter | Test Adapter |
| --- | --- | --- |
| Account persistence | PostgreSQL/jOOQ | in-memory account store |
| Social persistence | PostgreSQL/jOOQ | in-memory social store |
| Message delivery | WebSocket/Backend | deterministic in-memory delivery |
| Object storage | Russian S3-compatible store | local S3-compatible container |
| Email delivery | approved transactional provider | captured local mailbox |
| Phone verification | approved SMS provider | deterministic code verifier |
| Secure local keys | OS-specific protected storage | ephemeral test keystore |
| Relay transport | Netty TCP/TLS | loopback tunnel |
| Clock | system UTC clock | controllable clock |

There is no generic repository Interface per database table. Persistence Seams
exist at Module behavior, not CRUD mechanics.

## Protocols

### HTTPS

Used for browser account flows, Profiles, skins, exports, reports, and signed
object-storage operations. Web sessions use secure, HTTP-only cookies with CSRF
protection.

### WebSocket

Used for presence, Friend Requests, encrypted Message Events, device events,
World Invitations, and server notices. Binary frames use versioned Protobuf
envelopes with event IDs, ordering information, and idempotent replay behavior.
STOMP is not used.

### Relay

The owner and each Guest create outbound TLS connections. Relay validates
short-lived signed Relay Tickets and pairs streams for one World Session. The
inner game stream is encrypted end to end between mod instances. Relay records
only operational metadata permitted by policy and law.

Relay does not:

- query PostgreSQL;
- persist worlds or game packets;
- authorize by Minecraft nickname;
- fall back to IP-revealing P2P;
- migrate a live TCP session between Relay nodes.

## Cryptography

- MLS RFC 9420 through Bouncy Castle `bcmls-jdk18on` 1.86 or newer approved
  patch release.
- One MLS group per Direct or Group Conversation.
- Each registered Device is an MLS member.
- `MLS_128_DHKEMX25519_AES128GCM_SHA256_Ed25519` is the initial cipher suite.
- Attachments use a separate random key and Tink Streaming AEAD.
- New Devices see new events by default; history transfer requires a trusted
  Device or an explicitly enabled encrypted backup.
- No operator master key or hidden decryption path.
- Security claims remain experimental until independent review.

## Data ownership

| Data | Owner | Durable store |
| --- | --- | --- |
| Accounts, Devices, login state, verified phone | Identity | PostgreSQL |
| Friend Requests, Friendships, Blocks | Social Graph | PostgreSQL |
| Profiles, privacy preferences, playtime | Profiles | PostgreSQL |
| Current online state | Presence | memory; minimal operational logs |
| World Sessions and grants | World Sessions | PostgreSQL plus ephemeral Relay state |
| Encrypted envelopes and attachment metadata | Messaging Delivery | PostgreSQL |
| Encrypted chat images | Messaging Delivery | Russian object storage |
| Sanitized skins | Skin Library | Russian object storage |
| Reports, sanctions, appeals | Moderation | PostgreSQL and isolated evidence storage |
| Plaintext messages and MLS secrets | registered Devices | encrypted local cache |

## Failure behavior

- Backend unavailable: cached history remains usable; new events queue locally;
  existing Relay streams continue only for their valid session lifetime.
- Relay unavailable: world connections fail; Accounts, Profiles, and messaging
  continue.
- Object storage unavailable: text continues; uncached images and skin uploads
  fail explicitly.
- Owner tunnel lost: Guests receive a connection-lost state; after a short grace
  period they reconnect through a new World Session.
- Capacity reached: existing world sessions are favored; new sessions fail with
  an honest capacity error rather than switching to P2P.

## Testing strategy

- Module tests exercise each public Interface.
- PostgreSQL and object-storage Adapters use Testcontainers.
- Toxiproxy covers latency, disconnection, duplication, and reordering.
- Protocol compatibility uses pinned Protobuf generators and Buf breaking checks.
- CryptoEngine runs official MLS test vectors and fuzzes untrusted decoders.
- Relay framing receives property and fuzz tests before public exposure.
- End-to-end tests run two Forge clients, Backend, PostgreSQL, object storage,
  and Relay.
- Backup restore and account deletion are executable acceptance scenarios.

## Observability

Spring Actuator and Micrometer emit OTLP telemetry. Attributes must not contain
Account IDs, usernames, full IP addresses, Conversation IDs, object keys,
tokens, message content, or keys. Operational dashboards cover availability,
latency, errors, traffic, capacity, email/SMS delivery, and backup age.

## Scaling

The Backend begins as one modular monolith. The first scaling action is vertical
capacity. Relay scales horizontally by adding stateless nodes; a World Session
stays on one node until it closes. Redis is not introduced until a second
Backend instance creates a proven need for shared ephemeral state.
