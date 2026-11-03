package dev.keeptogether.relay;

import dev.keeptogether.protocol.relay.v1.RelayHandshake;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class RelayHandshakeDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) {
        if (input.readableBytes() < RelayHandshake.HEADER_BYTES) {
            return;
        }

        input.markReaderIndex();
        for (byte expected : RelayHandshake.MAGIC) {
            if (input.readByte() != expected) {
                throw new CorruptedFrameException("Invalid relay handshake");
            }
        }

        var ticketLength = input.readUnsignedShort();
        if (ticketLength == 0 || ticketLength > RelayHandshake.MAX_TICKET_BYTES) {
            throw new CorruptedFrameException("Invalid relay handshake");
        }
        if (input.readableBytes() < ticketLength) {
            input.resetReaderIndex();
            return;
        }

        output.add(input.readCharSequence(ticketLength, StandardCharsets.US_ASCII).toString());
    }
}
