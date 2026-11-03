# Threat model

## Status

This document describes intended controls, not a security certification. The
implementation must be independently reviewed before Keep Together claims
production-grade E2E security.

## Protected assets

- Account credentials, passkeys, sessions, and recovery material.
- Device identity and MLS secrets.
- Message and Attachment plaintext.
- Friendship, Block, Profile, and playtime data.
- Phone numbers, email addresses, IP addresses, and operational metadata.
- World access decisions and Relay Tickets.
- Release-signing, infrastructure, backup, and administrative keys.

## Trust zones

### Registered Device

Trusted to handle that Device's plaintext and keys. A compromised Minecraft
client, operating system, or malicious mod can read content visible on that
Device. E2E does not prevent screenshots or endpoint malware.

### Backend

Trusted to authenticate Accounts, order encrypted events, enforce metadata
permissions, and issue Relay Tickets. It is not trusted with chat plaintext or
MLS decryption keys.

### Relay

Trusted only to validate Relay Tickets, apply traffic limits, and forward
encrypted streams. It is not trusted with world content, Accounts, or durable
user data.

### Administrator

Trusted to act on disclosed Report evidence and operational metadata under
audit. Administrative access must not decrypt arbitrary Conversations.

### External processors

Email, SMS, hosting, DNS, and storage providers receive only the data required
for their function. Their legal and security posture is a launch gate.

## Adversaries

- An unauthenticated Internet attacker.
- A malicious or modified mod client.
- An abusive authenticated Account.
- A malicious Guest in a World Session.
- A stolen Device or stolen session token.
- A compromised Backend, Relay, object store, or dependency.
- A malicious insider with administrative access.
- A supply-chain attacker targeting CI, artifacts, or updates.

## Primary threats and controls

| Threat | Intended controls |
| --- | --- |
| Credential stuffing | Argon2id, rate limits, passkeys, TOTP, breach-password checks |
| Session theft | short access tokens, rotating hashed refresh tokens, reuse detection |
| Device insertion | old-device notification, safety numbers, device revocation, later key transparency |
| Database disclosure | E2E payloads, encryption at rest, least privilege, Russian residency |
| Message tampering/replay | MLS authentication, stable event IDs, ordering and idempotency |
| Attachment parser attack | decode/re-encode before encryption, strict dimensions, recipient revalidation |
| Nickname impersonation | authorization by Account ID and Device identity, not GameProfile name |
| Unauthorized world join | per-Guest Relay Ticket, Access Grant checks, ticket replay prevention |
| Host IP discovery | relay-only transport; no automatic direct fallback |
| Relay denial of service | provider protection, connection/rate/traffic limits, bounded frames |
| Spam | friendship requirement, phone verification, rate limits, blocks, sanctions |
| Admin compromise | separate admin Account, passkey/TOTP, short sessions, audit log |
| Malicious update | signed artifacts, checksums, SBOM, manual production deployment |
| Secret leakage | secret scanning, no repository secrets, separate environment credentials |

## Known limitations

- The server sees participants, timing, sizes, IPs, and delivery metadata.
- A compromised Device can read content available on that Device.
- JVM memory does not guarantee perfect secret erasure.
- Delete-for-everyone cannot erase screenshots or hostile local copies.
- A modified client can falsify playtime and some client-originated telemetry.
- MLS library correctness does not prove integration correctness.
- Russian legal requirements may make the agreed zero-knowledge messaging model
  unavailable for public launch.

## Security invariants

1. No Backend or Relay Interface accepts a Minecraft nickname as authorization.
2. No server-side code path can request arbitrary chat plaintext.
3. Every Device addition is visible to the Account and detectable by peers.
4. Every Relay stream maps to one Account, Device, World Session, and grant.
5. Untrusted lengths are validated before allocation.
6. Secrets never enter logs, traces, crash reports, or analytics.
7. Security-sensitive changes require negative tests and documented review.
