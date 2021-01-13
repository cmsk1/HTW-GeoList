package com.htwberlin.geolist.net;

import com.htwberlin.geolist.net.packet.PacketInputStream;
import com.htwberlin.geolist.net.packet.PacketOutputStream;
import com.htwberlin.geolist.net.packet.PacketType;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PacketStreamTest {
    @Test
    public void packetHeaderGetsWrittenAndRead() throws IOException {
        PacketOutputStream output = new PacketOutputStream(PacketType.SNC);
        byte[] encoded = output.encode();

        PacketInputStream input = new PacketInputStream(encoded);
        int type = input.getPacketType();

        assertEquals(PacketType.SNC, type);
    }

    @Test
    public void packetDataCorrectlyEncoded() throws IOException {
        PacketOutputStream output = new PacketOutputStream(PacketType.SNC);
        String[] originalArray = new String[] { "Data0", "Data1", "Data2" };
        int originalInt = 4278;
        String originalString = "Java";
        output.writeSerializable(originalArray);
        output.writeInt(originalInt);
        output.writeUTF(originalString);
        byte[] encoded = output.encode();

        PacketInputStream input = new PacketInputStream(encoded);
        int type = input.getPacketType();
        String[] transArray = input.readSerializable();
        int transInt = input.readInt();
        String transString = input.readUTF();

        assertEquals(PacketType.SNC, type);
        assertArrayEquals(originalArray, transArray);
        assertEquals(originalInt, transInt);
        assertEquals(originalString, transString);
    }
}
