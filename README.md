# Keep Together

Keep Together is an open-source social mod for Minecraft Java Edition. It is
designed to provide independent accounts, friendships, encrypted chats, custom
skins, and temporary friend-hosted worlds without a custom launcher.

> **Status:** pre-alpha technical spikes. A one-owner/one-guest development
> world tunnel is implemented; accounts, chat, production authorization,
> transport security, and cryptography are not implemented yet.
> Do not use this project for sensitive communications.

The first supported target is Minecraft Java Edition 1.20.1 with Forge and
Java 17. Other game versions and mod loaders may be added after the first
implementation is stable.

## Product shape

- A durable Keep Together identity is separate from a Minecraft nickname.
- Worlds run on the owner's computer and exist only while the owner is online.
- A relay forwards encrypted Minecraft TCP traffic without storing the world.
- Direct and small-group chats use end-to-end encryption.
- Profiles, friendships, privacy settings, and skin libraries are managed by a
  central backend.
- The public product is free and has no ads, paid features, or launcher.

The complete scope and non-goals are documented in [PRODUCT.md](PRODUCT.md).

## Repository map

| Path | Purpose |
| --- | --- |
| `mod/` | Forge mod and Minecraft-facing adapters |
| `backend/` | Modular monolith for identity, social data, delivery, and moderation |
| `relay/` | Stateless TCP relay for temporary world sessions |
| `web/` | Static account and profile website |
| `protocol/` | Versioned Protobuf schemas and generated types |
| `crypto/` | Client-side cryptographic module and test vectors |
| `test-support/` | In-memory adapters and cross-process test fixtures |
| `infra/` | Local, staging, and production deployment definitions |
| `docs/` | Architecture, threat model, data flows, legal gates, and ADRs |

Start with [ARCHITECTURE.md](ARCHITECTURE.md), [GLOSSARY.md](GLOSSARY.md), and
[ROADMAP.md](ROADMAP.md).

## Verify the foundation

On Windows, run `./scripts/check.ps1`. It builds and tests the Java services,
the Forge mod, and the website using pinned wrappers and package versions. JDK
17 and 25 are provisioned through Gradle toolchains when absent; Node 24-26 is
required for the website.

Local PostgreSQL, S3-compatible object storage, and captured email are defined
in `infra/local/compose.yaml`. They are not required for the M0 placeholder
applications; setup commands are in `docs/development/setup.md`.

The first two-client world-tunnel test is documented in
[`docs/development/m1-world-tunnel.md`](docs/development/m1-world-tunnel.md).

## Legal and launch status

Public launch is explicitly blocked until the project receives qualified
Russian IT/telecom legal review. Open questions include personal-data
localization, organizer-of-information-dissemination and messenger status,
phone identification, lawful retention, end-to-end encryption obligations, and
the legal status of the relay.

The project also does not verify ownership of Minecraft through Microsoft.
That choice carries acknowledged risk under Minecraft's EULA and Usage
Guidelines. The repository must not distribute Minecraft, Mojang assets, a
launcher, or modified game binaries.

Keep Together is not an official Minecraft product and is not approved by or
associated with Mojang or Microsoft.

## Contributing and security

See [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request. Report
security issues privately as described in [SECURITY.md](SECURITY.md); do not
publish exploitable details in a public issue.

## License

Keep Together is licensed under the [MIT License](LICENSE). Commercial and
closed-source forks are permitted when the license notice is preserved.
