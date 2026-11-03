# Roadmap

Dates are intentionally omitted. Progress is based on exit criteria rather
than calendar promises.

## M0: Repository foundation

- Establish repository policy, MIT licensing, DCO, security reporting, and ADRs.
- Add independent build roots for the mod, server processes, and website.
- Add protocol generation, CI, dependency scanning, secret scanning, and SBOM.
- Establish local, staging, and production configuration shapes.

**Exit:** clean bootstrap on a new machine and all checks pass without secrets.

## M1: Technical spikes

- Open an IntegratedServer through a local tunnel adapter.
- Join through a real relay from a second Forge client.
- Exchange a test MLS message between two device identities.
- Render a Keep Together display name and skin without changing GameProfile.

**Exit:** each risk is demonstrated and its constraints are documented.

## M2: Identity

- Registration, email verification, passkeys, password, and TOTP.
- Device authorization flow and rotating sessions.
- Account recovery, device revocation, and emergency account protection.
- Separate age confirmation and legal-document acceptance records.

**Exit:** two devices can be added and revoked without exposing credentials to
the mod.

## M3: Social and profiles

- Profiles, privacy settings, exact-username lookup, and playtime.
- Friend requests, friendships, blocks, and presence.
- Website account management and authenticated profile pages.

**Exit:** privacy defaults and every social edge case have integration tests.

## M4: World sessions

- World access grants, invitations, signed relay tickets, and reverse tunnels.
- Owner controls, guest removal, capacity reporting, and failure behavior.
- Mod-set compatibility report without automatic downloads.

**Exit:** seven guests can join a test world and unauthorized accounts cannot.

## M5: Skin library and in-game identity

- Skin validation, storage, library management, and active-skin fallback.
- Display names, profile cards, and non-member indicators in hosted worlds.

**Exit:** identity rendering is compatible with the underlying GameProfile and
common Forge behavior.

## M6: Direct conversations

- MLS device membership, offline delivery, encrypted cache, and key rotation.
- Text events, replies, edits, reactions, and best-effort deletion.
- Safety numbers, new-device warnings, recovery, and report evidence export.

**Exit:** official test vectors pass and Backend cannot read message plaintext.

## M7: Groups and images

- Group ownership, membership changes, and epoch transitions.
- Local image sanitization, streaming encryption, object storage, and quotas.
- Blocking behavior in shared groups and local cache eviction.

**Exit:** membership changes prevent removed Devices from reading new events.

## M8: Moderation and operational readiness

- Reports, sanctions, appeals, admin isolation, and access auditing.
- Data export, account deletion, backups, restore drills, and incident response.
- Monitoring, status page, global kill switches, and load tests.

**Exit:** a complete incident and recovery exercise succeeds on staging.

## M9: Public-beta gate

- Resolve Russian personal-data, OРI/messenger, phone-verification, E2EE, and
  relay legal questions with qualified counsel.
- Deploy Russian data residency and approved processors.
- Complete external security review and remediate critical findings.
- Publish signed artifacts, installation documentation, legal documents, and
  support channels.

**Exit:** every item in `docs/legal-launch-gates.md` is signed off and the
public-beta readiness checklist passes.

## Later possibilities

- Additional Minecraft versions.
- Additional mod loaders.
- Additional relay regions when legally and operationally approved.

These are separate projects, not abstractions to build before M9.
