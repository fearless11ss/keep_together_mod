package dev.keeptogether.tunnel;

import dev.keeptogether.protocol.relay.v1.RelayHandshake;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SocketWorldTunnel implements AutoCloseable {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration PAIR_TIMEOUT = Duration.ofSeconds(30);

    private final InetSocketAddress relayAddress;
    private final String ticket;
    private final InetSocketAddress targetAddress;
    private final ServerSocket localListener;
    private final ExecutorService executor;
    private final List<Closeable> resources = new ArrayList<>();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final CompletableFuture<Void> ready = new CompletableFuture<>();
    private final CompletableFuture<Void> completion = new CompletableFuture<>();

    private SocketWorldTunnel(
        InetSocketAddress relayAddress,
        String ticket,
        InetSocketAddress targetAddress,
        ServerSocket localListener,
        String threadPrefix
    ) {
        this.relayAddress = Objects.requireNonNull(relayAddress, "relayAddress");
        this.ticket = requireTicket(ticket);
        this.targetAddress = targetAddress;
        this.localListener = localListener;
        this.executor = Executors.newCachedThreadPool(daemonThreads(threadPrefix));
        if (localListener != null) {
            track(localListener);
        }
    }

    public static SocketWorldTunnel openOwner(
        InetSocketAddress relayAddress,
        String ownerTicket,
        InetSocketAddress worldAddress
    ) {
        var tunnel = new SocketWorldTunnel(
            relayAddress,
            ownerTicket,
            Objects.requireNonNull(worldAddress, "worldAddress"),
            null,
            "keep-together-owner-tunnel");
        tunnel.executor.execute(tunnel::runOwner);
        return tunnel;
    }

    public static SocketWorldTunnel openGuest(
        InetSocketAddress relayAddress,
        String guestTicket,
        InetSocketAddress localBindAddress
    ) throws IOException {
        Objects.requireNonNull(localBindAddress, "localBindAddress");
        var listener = new ServerSocket();
        listener.bind(localBindAddress);
        var tunnel = new SocketWorldTunnel(
            relayAddress,
            guestTicket,
            null,
            listener,
            "keep-together-guest-tunnel");
        tunnel.executor.execute(tunnel::runGuest);
        return tunnel;
    }

    public Optional<InetSocketAddress> localAddress() {
        if (localListener == null) {
            return Optional.empty();
        }
        return Optional.of((InetSocketAddress) localListener.getLocalSocketAddress());
    }

    public CompletableFuture<Void> ready() {
        return ready.copy();
    }

    public CompletableFuture<Void> completion() {
        return completion.copy();
    }

    @Override
    public void close() {
        close(null);
    }

    private void runOwner() {
        try {
            var relay = connect(relayAddress);
            authenticate(relay);
            var world = connect(targetAddress);
            ready.complete(null);
            startPumps(relay, world);
        } catch (Throwable failure) {
            close(failure);
        }
    }

    private void runGuest() {
        try {
            var localClient = track(localListener.accept());
            var relay = connect(relayAddress);
            authenticate(relay);
            ready.complete(null);
            startPumps(localClient, relay);
        } catch (Throwable failure) {
            close(failure);
        }
    }

    private Socket connect(SocketAddress address) throws IOException {
        var socket = track(new Socket());
        socket.setTcpNoDelay(true);
        socket.connect(address, Math.toIntExact(CONNECT_TIMEOUT.toMillis()));
        return socket;
    }

    private void authenticate(Socket relay) throws IOException {
        relay.setSoTimeout(Math.toIntExact(PAIR_TIMEOUT.toMillis()));
        relay.getOutputStream().write(RelayHandshake.encode(ticket));
        relay.getOutputStream().flush();
        if (relay.getInputStream().read() != RelayHandshake.READY) {
            throw new IOException("Relay rejected or closed the tunnel handshake");
        }
        relay.setSoTimeout(0);
    }

    private void startPumps(Socket left, Socket right) {
        executor.execute(() -> copy(left, right));
        executor.execute(() -> copy(right, left));
    }

    private void copy(Socket source, Socket destination) {
        try {
            source.getInputStream().transferTo(destination.getOutputStream());
            destination.getOutputStream().flush();
            close(null);
        } catch (IOException failure) {
            close(failure);
        }
    }

    private synchronized <T extends Closeable> T track(T resource) {
        if (closed.get()) {
            try {
                resource.close();
            } catch (IOException ignored) {
                // The tunnel is already closing; there is no caller left to notify.
            }
            throw new IllegalStateException("Tunnel is already closed");
        }
        resources.add(resource);
        return resource;
    }

    private void close(Throwable failure) {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        synchronized (this) {
            for (var resource : resources) {
                try {
                    resource.close();
                } catch (IOException ignored) {
                    // Preserve the first failure, if any.
                }
            }
            resources.clear();
        }
        executor.shutdownNow();
        if (failure == null) {
            ready.completeExceptionally(new IOException("Tunnel closed before it became ready"));
            completion.complete(null);
        } else {
            ready.completeExceptionally(failure);
            completion.completeExceptionally(failure);
        }
    }

    private static String requireTicket(String ticket) {
        if (ticket == null || ticket.isBlank()) {
            throw new IllegalArgumentException("ticket must not be blank");
        }
        return ticket;
    }

    private static ThreadFactory daemonThreads(String prefix) {
        return runnable -> {
            var thread = new Thread(runnable, prefix);
            thread.setDaemon(true);
            return thread;
        };
    }
}
