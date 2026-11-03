package dev.keeptogether.protocol.relay.v1;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class HmacRelayTicketCodec {

    private static final int VERSION = 1;
    private static final int PAYLOAD_BYTES = 1 + 1 + 16 + 16 + Long.BYTES;
    private static final int MINIMUM_SECRET_BYTES = 32;
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final SecretKeySpec key;
    private final Clock clock;

    public HmacRelayTicketCodec(byte[] secret, Clock clock) {
        Objects.requireNonNull(secret, "secret");
        this.clock = Objects.requireNonNull(clock, "clock");
        if (secret.length < MINIMUM_SECRET_BYTES) {
            throw new IllegalArgumentException("Relay ticket secret must contain at least 32 bytes");
        }
        this.key = new SecretKeySpec(secret.clone(), HMAC_ALGORITHM);
    }

    public String issue(RelayTicketClaims claims) {
        Objects.requireNonNull(claims, "claims");
        var payload = ByteBuffer.allocate(PAYLOAD_BYTES)
            .put((byte) VERSION)
            .put((byte) claims.role().wireValue());
        putUuid(payload, claims.ticketId());
        putUuid(payload, claims.sessionId());
        payload.putLong(claims.expiresAt().toEpochMilli());

        var payloadBytes = payload.array();
        return ENCODER.encodeToString(payloadBytes) + "." + ENCODER.encodeToString(sign(payloadBytes));
    }

    public RelayTicketClaims verify(String encodedTicket) {
        try {
            var parts = Objects.requireNonNull(encodedTicket, "encodedTicket").split("\\.", -1);
            if (parts.length != 2) {
                throw new InvalidRelayTicketException();
            }

            var payloadBytes = DECODER.decode(parts[0]);
            var suppliedSignature = DECODER.decode(parts[1]);
            if (payloadBytes.length != PAYLOAD_BYTES
                || !MessageDigest.isEqual(sign(payloadBytes), suppliedSignature)) {
                throw new InvalidRelayTicketException();
            }

            var payload = ByteBuffer.wrap(payloadBytes);
            if (Byte.toUnsignedInt(payload.get()) != VERSION) {
                throw new InvalidRelayTicketException();
            }
            var role = RelayRole.fromWireValue(Byte.toUnsignedInt(payload.get()));
            var ticketId = readUuid(payload);
            var sessionId = readUuid(payload);
            var expiresAt = java.time.Instant.ofEpochMilli(payload.getLong());
            if (!expiresAt.isAfter(clock.instant())) {
                throw new InvalidRelayTicketException();
            }
            return new RelayTicketClaims(ticketId, sessionId, role, expiresAt);
        } catch (IllegalArgumentException | NullPointerException exception) {
            if (exception instanceof InvalidRelayTicketException invalidTicket) {
                throw invalidTicket;
            }
            throw new InvalidRelayTicketException();
        }
    }

    private byte[] sign(byte[] payload) {
        try {
            var mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(key);
            return mac.doFinal(payload);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("HMAC-SHA256 is unavailable", exception);
        }
    }

    private static void putUuid(ByteBuffer buffer, UUID value) {
        buffer.putLong(value.getMostSignificantBits());
        buffer.putLong(value.getLeastSignificantBits());
    }

    private static UUID readUuid(ByteBuffer buffer) {
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
