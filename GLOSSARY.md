# Keep Together

Canonical product language for identity, social relationships, messaging,
skins, and temporary friend-hosted worlds.

## Identity

**Account**:
A person's durable Keep Together identity. It survives changes to names,
devices, skins, and Minecraft nicknames.
_Avoid_: User record, profile, Minecraft account

**Account ID**:
The immutable identifier of an Account.
_Avoid_: Username, UUID when the domain meaning is Account ID

**Username**:
The unique, case-insensitive `@name` used to find an Account.
_Avoid_: Login, Minecraft name, display name

**Display Name**:
The mutable name shown for an Account in Keep Together interfaces and hosted
worlds.
_Avoid_: Username, nickname when the distinction matters

**Device**:
A registered installation authorized to act for an Account.
_Avoid_: Session, computer

**Minecraft Player**:
A participant represented by Minecraft's game identity during a world
connection. A Minecraft Player is not proof of a Keep Together Account.
_Avoid_: Account, verified user

## Social graph

**Friend Request**:
A pending proposal from one Account to form a Friendship with another Account.
_Avoid_: Invite when referring to friendship

**Friendship**:
A mutual, confirmed relationship between two Accounts.
_Avoid_: Follow, contact

**Block**:
A unilateral relationship that suppresses profile, presence, messaging, and
world access between two Accounts.
_Avoid_: Ban, mute

**Presence**:
The privacy-filtered availability state of an Account.
_Avoid_: Activity log, location

## Worlds

**World Owner**:
The Account whose computer runs the IntegratedServer for a World Session.
_Avoid_: Server owner, host machine

**World Session**:
The temporary period during which a World Owner exposes one loaded world to
authorized friends.
_Avoid_: Server, hosted world, realm

**Guest**:
An Account authorized to join a World Session.
_Avoid_: Player when the access relationship matters

**Access Grant**:
The World Owner's permission for an Account to join a World Session.
_Avoid_: Invite, whitelist entry

**World Invitation**:
A short-lived invitation for one Account to join one World Session.
_Avoid_: Access Grant, permanent link

**Relay**:
The intermediary that forwards encrypted game traffic between a World Owner
and Guests without running or storing the world.
_Avoid_: Game server, proxy when the domain role matters

**Relay Ticket**:
A short-lived, single-purpose authorization to connect to a Relay for one World
Session.
_Avoid_: Session token, invite code

## Messaging

**Conversation**:
An end-to-end encrypted message history shared by a defined membership.
_Avoid_: Channel, room

**Direct Conversation**:
A Conversation between the registered Devices of two Accounts.
_Avoid_: Private channel, DM thread

**Group Conversation**:
An invite-only Conversation containing at most ten Accounts.
_Avoid_: Server, community, public group

**Message Event**:
An authenticated encrypted event that creates, edits, reacts to, or deletes a
message.
_Avoid_: Message when the event distinction matters

**Attachment**:
An encrypted static image referenced by a Message Event.
_Avoid_: File, media when arbitrary content is implied

**Recovery Secret**:
A user-held secret that can unlock an encrypted history backup.
_Avoid_: Password, recovery code

## Profiles and skins

**Profile**:
The privacy-filtered presentation of an Account to other authenticated
Accounts.
_Avoid_: Account, public page

**Skin Library**:
An Account's private collection of validated Minecraft skins.
_Avoid_: Gallery, profile images

**Active Skin**:
The Skin selected for display to permitted Keep Together members.
_Avoid_: Avatar

## Trust and operations

**Trusted Tester**:
An invited participant in the private alpha environment.
_Avoid_: Beta user, public user

**Private Alpha**:
The allowlisted environment used by Trusted Testers before public launch.
_Avoid_: Production, public beta

**Public Beta**:
The first production stage with registration open to an unspecified set of
eligible users.
_Avoid_: Stable release

**Report**:
A user's submission of selected evidence for moderation review.
_Avoid_: Automatic conviction, telemetry

**Sanction**:
A recorded restriction imposed on a specific Account capability.
_Avoid_: Block, kick
