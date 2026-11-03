# Product definition

## Outcome

Keep Together lets friends maintain a stable social identity, communicate, use
their own skins, and join a temporary Minecraft world hosted on a friend's
computer. It does not replace Minecraft, distribute Minecraft, or provide a
launcher.

The product begins as a private alpha for trusted testers. Public registration
is a later milestone gated by security, operational, and legal readiness.

## Initial audience

- Russian-speaking adults who confirm that they are at least 18 years old.
- Minecraft Java Edition 1.20.1 players using Forge.
- Friends who want private worlds rather than public server discovery.

Age confirmation is a self-declaration, not documentary age verification.

## Core capabilities

### Identity and profiles

- One durable internal account ID independent of Minecraft and Microsoft.
- A case-insensitive unique `@username` and a mutable display name.
- Public-key device identities, passkeys, passwords, TOTP, and recovery codes.
- One verified mobile number per account before messaging is enabled.
- Profiles visible only to authenticated Keep Together accounts.
- Privacy controls for presence, playtime, skins, typing, and read receipts.
- Playtime counts authenticated, non-AFK time spent in a loaded world.

### Friends and presence

- Mutual friendship confirmation.
- At most 500 friends and 100 pending incoming/outgoing requests.
- Blocks override presence, messaging, profile, and world access.
- Exact-username discovery; no global directory or recommendations.

### Temporary worlds

- The owner opens a loaded single-player world from the pause menu.
- Access is granted to all friends or a selected subset.
- The world runs entirely on the owner's IntegratedServer.
- An encrypted reverse TCP tunnel crosses NAT/CGNAT without exposing the
  owner's IP address to guests.
- The Minecraft 1.20.1 vanilla limit remains eight total players: the owner
  plus seven guests.
- The session ends when the owner closes the world or leaves the game.

### Messaging

- Direct chats between friends.
- Invite-only group chats with at most ten members.
- Text, replies, edits, best-effort deletion, reactions, typing indicators, and
  optional read receipts.
- End-to-end encrypted offline delivery and encrypted local caching.
- Static PNG, JPEG, and WebP images only; no animation.
- Up to four images per message, 10 MiB each, 4096 x 4096 pixels maximum.
- 100 MiB total server-side image quota per account.
- No link previews; external links require an explicit confirmation.

Messaging is a legal launch gate. If Russian law requires a model incompatible
with the agreed security properties, public messaging remains disabled until a
lawful design is approved.

### Skins

- A private library of at most 100 vanilla-compatible PNG skins.
- Steve and Alex arm models.
- One active skin, synchronized across devices.
- Deleting the active skin selects the most recently used remaining skin; the
  vanilla Minecraft skin is used only when the library is empty.
- Keep Together identities and other members' skins are guaranteed only inside
  Keep Together-hosted worlds.

## Privacy defaults

- Profiles require authentication and are excluded from search indexing.
- Presence and playtime are visible to friends only.
- The skin library is visible only to its owner; the active skin is visible to
  friends.
- Friend discovery requires the exact `@username`.
- No contact-book upload, advertising cookies, cross-site tracking, behavioral
  analytics, or device fingerprinting.
- Telemetry is opt-in and excludes message content, keys, tokens, usernames,
  account IDs, chat IDs, object keys, and full IP addresses.

## Moderation and account lifecycle

- Profile, skin, account, world, and messaging restrictions are separate
  sanctions.
- Reports disclose only user-selected decrypted evidence.
- Report evidence is access-logged and removed after the case-retention period.
- Account deletion has a seven-day cancellation period.
- Product data is deleted after that period, except information the operator is
  legally required to retain.
- Users can export profile, friendship, device, skin, and locally decrypted
  chat data.

## Permanent non-goals

- A custom launcher or distribution of Minecraft binaries.
- Voice messages, voice calls, video, arbitrary files, or public posts.
- A public world browser or access for strangers.
- Cloud world storage, world synchronization, or worlds that run without the
  owner.
- Automatic download or installation of third-party mods.
- Paid features, advertising, subscriptions, or gameplay advantages.

Other Minecraft versions and mod loaders are future possibilities, not
permanent non-goals.

## Product constraints

- Solo development at approximately ten hours per week.
- Initial infrastructure budget target: EUR 50 per month.
- Russian data residency for Russian users unless qualified counsel approves a
  different lawful arrangement.
- Public source code from the beginning under the MIT License.
- Public beta is blocked until the gates in
  [docs/legal-launch-gates.md](docs/legal-launch-gates.md) are satisfied.

## Stage success criteria

### Technical prototype

- Two Forge clients connect through a real relay.
- A guest joins the owner's IntegratedServer without router port forwarding.
- Access is authorized by Keep Together account ID rather than Minecraft name.
- A test MLS message is readable only by the intended devices.
- Display-name and skin rendering do not mutate the underlying GameProfile.

### Private alpha

- Identity, friendships, worlds, profiles, skins, and E2E text are usable by
  trusted testers.
- A stolen device can be revoked.
- PostgreSQL restore is proven.
- Several weeks pass without critical data loss.

### Public beta

- Legal gates are resolved.
- Phone verification, reports, export, and deletion work end to end.
- Load, security, recovery, and protocol-compatibility tests pass.
- Signed releases, documentation, incident response, and status reporting are
  operational.
