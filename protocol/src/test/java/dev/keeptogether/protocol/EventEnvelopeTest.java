package dev.keeptogether.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.ByteString;
import dev.keeptogether.protocol.envelope.v1.EventEnvelope;
import org.junit.jupiter.api.Test;

class EventEnvelopeTest {

    @Test
    void roundTripsStableEnvelopeFields() throws Exception {
        var envelope = EventEnvelope.newBuilder()
            .setEventId("01JTESTEVENT000000000000000")
            .setSchemaVersion(1)
            .setSequence(42)
            .setPayload(ByteString.copyFromUtf8("opaque"))
            .build();

        var decoded = EventEnvelope.parseFrom(envelope.toByteArray());

        assertThat(decoded).isEqualTo(envelope);
    }
}
