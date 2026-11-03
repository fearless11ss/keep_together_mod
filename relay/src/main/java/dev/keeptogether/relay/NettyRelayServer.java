package dev.keeptogether.relay;

import dev.keeptogether.protocol.relay.v1.HmacRelayTicketCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class NettyRelayServer implements RelayServer {

    private static final Duration HANDSHAKE_TIMEOUT = Duration.ofSeconds(10);

    private final InetSocketAddress bindAddress;
    private final HmacRelayTicketCodec ticketCodec;
    private final RelayPairRegistry pairRegistry = new RelayPairRegistry();
    private final EventLoopGroup acceptGroup = new MultiThreadIoEventLoopGroup(
        1,
        NioIoHandler.newFactory());
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(
        NioIoHandler.newFactory());
    private Channel serverChannel;

    public NettyRelayServer(InetSocketAddress bindAddress, HmacRelayTicketCodec ticketCodec) {
        this.bindAddress = Objects.requireNonNull(bindAddress, "bindAddress");
        this.ticketCodec = Objects.requireNonNull(ticketCodec, "ticketCodec");
    }

    @Override
    public synchronized InetSocketAddress start() throws InterruptedException {
        if (serverChannel != null) {
            throw new IllegalStateException("Relay server is already started");
        }
        serverChannel = new ServerBootstrap()
            .group(acceptGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    channel.pipeline()
                        .addLast(new ReadTimeoutHandler(
                            HANDSHAKE_TIMEOUT.toSeconds(),
                            TimeUnit.SECONDS))
                        .addLast(new RelayHandshakeDecoder())
                        .addLast(new RelayHandshakeHandler(ticketCodec, pairRegistry));
                }
            })
            .bind(bindAddress)
            .sync()
            .channel();
        return (InetSocketAddress) serverChannel.localAddress();
    }

    @Override
    public synchronized void close() {
        if (serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
            serverChannel = null;
        }
        acceptGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }
}
