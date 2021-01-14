package com.htwberlin.geolist.net.protocol;

import com.htwberlin.geolist.Utils;
import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.packet.PacketInputStream;
import com.htwberlin.geolist.net.packet.PacketOutputStream;

import java.io.IOException;

public abstract class AbstractProtocol implements IProtocol {
    private IProtocolState state;
    private IChannel channel;

    public AbstractProtocol() {
        this.state = this::start;
    }

    @Override
    public void handleConnection(IChannel channel) throws IOException {
        this.channel = channel;

        do {
            this.state = this.state.transition();
        } while (this.state != null);

        Utils.sleepSafe(2000);
    }

    protected void send(PacketOutputStream packet) throws IOException {
        this.channel.send(packet);
    }

    protected void send(int packetType) throws IOException {
        this.send(new PacketOutputStream(packetType));
    }

    protected PacketInputStream receive() throws IOException {
        return this.channel.receive();
    }

    protected PacketInputStream receive(int packetType) throws IOException {
        PacketInputStream packet = this.channel.receive();
        int decodedType = packet.getPacketType();

        if (decodedType != packetType) {
            throw new IllegalProtocolState(packetType, decodedType);
        }
        return packet;
    }

    public Device getPeerDevice() {
        return this.channel.getPeerDevice();
    }

    protected abstract IProtocolState start() throws IOException;
}
