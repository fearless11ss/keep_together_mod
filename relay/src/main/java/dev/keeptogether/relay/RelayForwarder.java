package dev.keeptogether.relay;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.ReferenceCountUtil;

final class RelayForwarder extends ChannelInboundHandlerAdapter {

    private final Channel peer;

    RelayForwarder(Channel peer) {
        this.peer = peer;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (!peer.isActive()) {
            ReferenceCountUtil.release(message);
            context.close();
            return;
        }
        peer.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        closePeer();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        context.close();
        closePeer();
    }

    private void closePeer() {
        if (peer.isOpen()) {
            peer.close();
        }
    }
}
