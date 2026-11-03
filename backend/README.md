# Backend

The Backend is a JDK 25 Spring Boot modular monolith. Planned Modules are
Identity, Social Graph, Profiles, Presence, World Sessions, Messaging Delivery,
Skin Library, and Moderation.

Spring Modulith will verify package relationships. A Module owns its data and
invariants; another Module calls its Interface rather than querying its tables.
The Backend stores encrypted message envelopes but no chat plaintext or MLS
decryption keys.
