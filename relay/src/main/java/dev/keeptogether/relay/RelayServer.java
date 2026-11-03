package dev.keeptogether.relay;

import java.net.InetSocketAddress;

public interface RelayServer extends AutoCloseable {

    InetSocketAddress start() throws InterruptedException;

    @Override
    void close();
}
