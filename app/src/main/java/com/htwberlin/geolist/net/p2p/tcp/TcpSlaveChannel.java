package com.htwberlin.geolist.net.p2p.tcp;

import com.htwberlin.geolist.Utils;
import com.htwberlin.geolist.net.p2p.ConnectionState;
import com.htwberlin.geolist.net.p2p.Device;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpSlaveChannel extends TcpChannel {
    private final SocketAddress socketAddress;
    private Socket socket;

    public TcpSlaveChannel(Device device, InetAddress masterAddress, int port) {
        this(device, new InetSocketAddress(masterAddress, port));
    }

    public TcpSlaveChannel(Device device, InetSocketAddress masterSockAddress) {
        super(device);
        this.socketAddress = masterSockAddress;
    }

    @Override
    protected void openSocket() throws IOException {
        this.socket = new Socket();
        this.socket.connect(this.socketAddress);
        this.submitSocket(this.socket);
    }

    @Override
    protected void closeSocket() {
        Utils.closeSafe(this.socket);
    }
}
