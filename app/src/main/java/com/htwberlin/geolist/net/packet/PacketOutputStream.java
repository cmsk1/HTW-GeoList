package com.htwberlin.geolist.net.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PacketOutputStream extends DataOutputStream {
    private final int packetType;

    public PacketOutputStream(int packetType) throws IOException {
        super(new ByteArrayOutputStream());
        this.packetType = packetType;
        this.writeInt(packetType);
    }

    public <T extends Serializable> void writeSerializable(T obj) throws IOException {
        ObjectOutputStream objEncoder = new ObjectOutputStream(this.out);
        objEncoder.writeObject(obj);
    }

    public int getPacketType() {
        return this.packetType;
    }

    public byte[] encode() {
        ByteArrayOutputStream encoded = (ByteArrayOutputStream) this.out;
        return encoded.toByteArray();
    }
}
