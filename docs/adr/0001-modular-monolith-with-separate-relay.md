# Use a modular monolith with a separate Relay process

The Backend will be one modular monolith with deep identity, social, profile,
presence, world, messaging-delivery, skin, and moderation Modules. Relay is a
separate stateless process because its traffic, failure modes, and scaling are
materially different; the remaining Modules stay in one process to preserve
transactional locality and avoid premature distributed systems.
