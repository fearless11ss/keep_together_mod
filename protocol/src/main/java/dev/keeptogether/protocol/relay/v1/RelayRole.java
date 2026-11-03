package dev.keeptogether.protocol.relay.v1;

public enum RelayRole {
    OWNER(1),
    GUEST(2);

    private final int wireValue;

    RelayRole(int wireValue) {
        this.wireValue = wireValue;
    }

    int wireValue() {
        return wireValue;
    }

    static RelayRole fromWireValue(int wireValue) {
        return switch (wireValue) {
            case 1 -> OWNER;
            case 2 -> GUEST;
            default -> throw new InvalidRelayTicketException();
        };
    }
}
