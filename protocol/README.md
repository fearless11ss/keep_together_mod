# Protocol

This directory will own versioned Protobuf schemas for mod/Backend WebSocket
events and Relay control frames. Generated Java targets Java 17; generated
TypeScript targets the Website toolchain.

Compatibility is enforced with pinned compiler/runtime versions and Buf
breaking-change checks. Domain behavior does not belong in generated types.
