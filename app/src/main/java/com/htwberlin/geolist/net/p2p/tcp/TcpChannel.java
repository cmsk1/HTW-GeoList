package com.htwberlin.geolist.net.p2p.tcp;

import com.htwberlin.geolist.net.p2p.AbstractChannel;
import com.htwberlin.geolist.net.p2p.ConnectionState;
import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.packet.PacketInputStream;
import com.htwberlin.geolist.net.packet.PacketOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class TcpChannel extends AbstractChannel implements IChannel {
    public static final int DEFAULT_TIMEOUT = 10000;

    protected DataOutputStream out;
    protected DataInputStream in;
    private TcpAliveThread timeout;
    private Device peerDevice;
    private Device device;
    private boolean isOpen;
    private int timeoutMillis;
    private boolean isSocketKilled;

    public TcpChannel(Device device) {
        this.device = device;
        this.timeoutMillis = TcpChannel.DEFAULT_TIMEOUT;
    }

    @Override
    public void open() throws IOException {
        if (!this.isOpen) {
            try {
                this.isOpen = true;
                this.fireStateChange(ConnectionState.CONNECTING);
                this.timeout = new TcpAliveThread(this::onTimeout, this.timeoutMillis);
                this.timeout.start();
                this.openSocket();
                this.sendDeviceHeader(this.device);
                this.peerDevice = this.receiveDeviceHeader();
                this.fireStateChange(ConnectionState.CONNECTED);
            } catch (IOException e) {
                this.fireStateChange(ConnectionState.UNABLE_TO_CONNECT);
                this.killSocket();
                throw e;
            }
        }
    }

    private void sendDeviceHeader(Device device) throws IOException {
        this.out.writeUTF(device.getSignature());
        this.out.writeUTF(device.getDisplayName());
    }

    private Device receiveDeviceHeader() throws IOException {
        String signature = this.in.readUTF();
        String displayName = this.in.readUTF();
        return new Device(signature, displayName);
    }

    protected void submitSocket(Socket socket) throws IOException {
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
    }

    public void setTimeout(int millis) {
        this.timeoutMillis = millis;
    }

    private void onTimeout() {
        this.fireStateChange(ConnectionState.MESSAGE_TIMEOUT);
        this.killSocket();
    }

    @Override
    public void send(PacketOutputStream packet) throws IOException {
        byte[] encoded = packet.encode();
        this.sendRaw(encoded);
    }

    @Override
    public void sendRaw(byte[] encoded) throws IOException {
        if (!this.isOpen) {
            throw new IOException("channel was used but not opened");
        }

        try {
            this.out.writeInt(encoded.length);
            this.out.write(encoded);
            this.out.flush();
        } catch (IOException e) {
            if (!this.isSocketKilled) {
                this.fireStateChange(ConnectionState.IO_FAILED);
            }
            throw e;
        }
    }

    @Override
    public PacketInputStream receive() throws IOException {
        byte[] encoded = this.receiveRaw();
        PacketInputStream packet = new PacketInputStream(encoded);
        return packet;
    }

    @Override
    public byte[] receiveRaw() throws IOException {
        if (!this.isOpen) {
            throw new IOException("channel was used but not opened");
        }

        try {
            int length = this.in.readInt();
            this.timeout.reset();
            byte[] encoded = new byte[length];
            this.in.read(encoded);
            return encoded;
        } catch (IOException e) {
            if (!this.isSocketKilled) {
                this.fireStateChange(ConnectionState.IO_FAILED);
            }
            throw e;
        }
    }

    @Override
    public void disconnect() {
        if (this.isOpen) {
            this.timeout.stop();
            this.killSocket();
            this.fireStateChange(ConnectionState.DISCONNECTED);
        }
    }

    private void killSocket() {
        this.isSocketKilled = true;
        this.closeSocket();
        this.isOpen = false;
    }

    @Override
    public Device getPeerDevice() {
        return this.peerDevice;
    }

    protected abstract void openSocket() throws IOException;
    protected abstract void closeSocket();
}
