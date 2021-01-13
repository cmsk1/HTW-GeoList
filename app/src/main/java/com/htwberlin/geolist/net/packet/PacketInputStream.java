package com.htwberlin.geolist.net.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class PacketInputStream extends DataInputStream {
    private final int packetType;

    public PacketInputStream(byte[] encoded) throws IOException {
        super(new ByteArrayInputStream(encoded));
        this.packetType = this.readInt();
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T readSerializable() throws IOException {
        ObjectInputStream objEncoder = new ObjectInputStream(this.in);

        try {
            return (T) objEncoder.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("unable to deserialize object", e);
        }
    }

    public int getPacketType() {
        return this.packetType;
    }
}
