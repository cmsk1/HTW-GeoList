package com.htwberlin.geolist.net;

import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.net.packet.PacketInputStream;
import com.htwberlin.geolist.net.packet.PacketOutputStream;
import com.htwberlin.geolist.net.packet.PacketType;
import com.htwberlin.geolist.net.protocol.GeoListProtocol;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class SpyGeoListProtocol extends GeoListProtocol {
    private final Queue<Integer> received = new LinkedList<>();

    public SpyGeoListProtocol(DataStorage storage, boolean isMaster, UUID shareTasklist) {
        super(storage, isMaster, shareTasklist);
    }

    @Override
    protected PacketInputStream receive() throws IOException {
        PacketInputStream packet = super.receive();
        received.add(packet.getPacketType());
        return packet;
    }

    public int getReceivedCount() {
        return this.received.size();
    }

    public void assertReceived(int expected) {
        if (this.received.size() == 0) Assertions.fail("expected to receive " + expected + " but got nothing");
        int actual = this.received.poll();
        if (actual != expected) Assertions.fail("expected to receive " + expected + " but got " + actual);
    }
}
