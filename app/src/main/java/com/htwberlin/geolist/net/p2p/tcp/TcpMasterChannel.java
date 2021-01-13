package com.htwberlin.geolist.net.p2p.tcp;

import com.htwberlin.geolist.Utils;
import com.htwberlin.geolist.net.p2p.ConnectionState;
import com.htwberlin.geolist.net.p2p.Device;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpMasterChannel extends TcpChannel {
    private final int port;
    private ServerSocket server;
    private Socket socket;

    public TcpMasterChannel(Device device, int port) {
        super(device);
        this.port = port;
    }

    @Override
    protected void openSocket() throws IOException {
        this.server = new ServerSocket(this.port);
        this.socket = server.accept();
        this.submitSocket(this.socket);
    }

    @Override
    protected void closeSocket() {
        Utils.closeSafe(this.socket);
        Utils.closeSafe(this.server);
    }
}
