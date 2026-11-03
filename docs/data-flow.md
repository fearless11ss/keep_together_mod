# Data flow and retention

This is an engineering inventory, not a final privacy policy. Legal bases,
exact retention periods, and processor contracts require Russian legal review.

## Data categories

| Category | Examples | Primary purpose | Planned location |
| --- | --- | --- | --- |
| Identity | email, encrypted phone, username, age confirmation | account access and legal eligibility | Russia / PostgreSQL |
| Device security | public keys, passkeys, session hashes, TOTP state | authenticate and revoke Devices | Russia / PostgreSQL |
| Social graph | requests, friendships, blocks | permission and discovery | Russia / PostgreSQL |
| Profiles | display name, bio, privacy, playtime | authenticated social profile | Russia / PostgreSQL |
| Messaging metadata | membership, event order, size, delivery | encrypted delivery | Russia / PostgreSQL |
| Chat content | MLS ciphertext, encrypted image objects | user communication | Russia / PostgreSQL and object storage |
| Skins | sanitized PNG, library metadata | in-game appearance | Russia / object storage and PostgreSQL |
| World metadata | session IDs, grants, tickets, capacity | temporary world access | Russia / PostgreSQL and Relay memory |
| Moderation | Reports, disclosed evidence, Sanctions, appeals | safety and enforcement | isolated Russian storage |
| Operations | short-lived IP and error logs, traffic, backup state | security and reliability | Russia / controlled logging stores |

## Important flows

### Account registration

The Website sends email, password-derived material, separate legal acceptances,
and age confirmation to Identity. Identity stores the minimum required fields,
sends a verification email through an approved processor, and returns an
opaque web session.

### Phone verification

Identity sends one E.164 number to the selected SMS Adapter. PostgreSQL stores
the encrypted normalized number and a separate HMAC for uniqueness. The number
is not searchable by other Accounts and never appears in telemetry.

### Device authorization

The mod creates a key pair and short code. The authenticated Website approves
the code. Identity registers the public Device key and delivers rotating Device
session material without exposing the user's password to the mod.

### Message delivery

The sender sanitizes any image, encrypts the Attachment locally, and uploads
ciphertext by a short-lived signed operation. CryptoEngine creates an MLS
Message Event containing the Attachment key and reference. Backend stores and
orders ciphertext only. The recipient decrypts and revalidates locally.

### World connection

Backend checks Friendship, Block, Access Grant, Device, and World Session state.
It signs a short-lived Relay Ticket. Relay verifies the ticket locally and
forwards an inner encrypted stream without querying PostgreSQL.

### Report

The reporting Device explicitly selects evidence, decrypts only that evidence,
and submits it to Moderation. Evidence moves into an isolated, access-logged
store and is not reintroduced into ordinary message storage.

## Retention intentions

- Profile and social data: while the Account exists, subject to legal necessity.
- Message history: no product-driven time expiry while the Account exists;
  mandatory legal periods and purpose limitation still apply.
- Report evidence: 90 days after case closure unless an appeal or law requires
  longer retention.
- Ordinary network logs: target seven days.
- Confirmed security-incident logs: target 30 days or the approved legal period.
- Backups: target 30 days, encrypted and stored separately in Russia.
- Deleted Account: seven-day cancellation period, then product deletion; legally
  required records are isolated until their mandatory period ends.

## Data minimization rules

- No contact-book upload.
- No advertising or cross-site analytics.
- No hardware identifiers or hidden browser fingerprint.
- No plaintext messages or images in server telemetry.
- No full phone numbers, emails, IPs, or object keys in metrics.
- No world names, coordinates, dimensions, or third-party server addresses in
  Backend presence.
