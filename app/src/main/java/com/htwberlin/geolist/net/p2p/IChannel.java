package com.htwberlin.geolist.net.p2p;

import com.htwberlin.geolist.net.packet.PacketInputStream;
import com.htwberlin.geolist.net.packet.PacketOutputStream;

import java.io.IOException;

public interface IChannel {
    void open() throws IOException;
    void send(PacketOutputStream packet) throws IOException;
    void sendRaw(byte[] raw) throws IOException;
    PacketInputStream receive() throws IOException;
    byte[] receiveRaw() throws IOException;
    void disconnect();
    Device getPeerDevice();

    void addListener(IChannelListener listener);
    void removeListener(IChannelListener listener);
}
