package com.htwberlin.geolist.net.p2p;

public enum ConnectionState {
    CONNECTING(false),
    CONNECTED(false),
    DISCONNECTED(false),
    UNABLE_TO_CONNECT(true),
    MESSAGE_TIMEOUT(true),
    IO_FAILED(true),
    ;

    private boolean isFailure;

    ConnectionState(boolean isFailure) {
        this.isFailure = isFailure;
    }

    public boolean isFailure() {
        return this.isFailure;
    }

    public boolean isActiveState() {
        return (this == CONNECTING) || (this == CONNECTED);
    }
}
