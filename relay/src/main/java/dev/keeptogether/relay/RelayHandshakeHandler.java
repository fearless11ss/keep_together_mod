package dev.keeptogether.relay;

import dev.keeptogether.protocol.relay.v1.HmacRelayTicketCodec;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

final class RelayHandshakeHandler extends SimpleChannelInboundHandler<String> {

    private final HmacRelayTicketCodec ticketCodec;
    private final RelayPairRegistry pairRegistry;

    RelayHandshakeHandler(HmacRelayTicketCodec ticketCodec, RelayPairRegistry pairRegistry) {
        this.ticketCodec = ticketCodec;
        this.pairRegistry = pairRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String ticket) {
        var claims = ticketCodec.verify(ticket);
        pairRegistry.register(context.channel(), claims);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        context.close();
    }
}
