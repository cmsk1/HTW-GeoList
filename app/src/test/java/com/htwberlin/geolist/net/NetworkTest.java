package com.htwberlin.geolist.net;

import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.packet.PacketInputStream;
import com.htwberlin.geolist.net.packet.PacketOutputStream;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NetworkTest {
    private Device masterDevice;
    private Device slaveDevice;
    private NullNetwork masterNetwork;
    private NullNetwork slaveNetwork;

    @BeforeEach
    public void beforeEach() {
        this.masterDevice = new Device("Master", "7137157823");
        this.slaveDevice = new Device("Slave", "587274163463");
        this.masterNetwork = new NullNetwork(masterDevice);
        this.slaveNetwork = new NullNetwork(slaveDevice);
    }

    @Test
    public void deviceInformationGetsExchanged() {
        masterNetwork.setNetworkListener((channel) -> assertEquals(slaveDevice, channel.getPeerDevice()));
        slaveNetwork.setNetworkListener((channel) -> assertEquals(masterDevice, channel.getPeerDevice()));

        masterNetwork.connect(slaveDevice);

        masterNetwork.testChannelThread();
        slaveNetwork.testChannelThread();
    }

    @Test
    public void deviceCanCommunicateOverChannels() {
        final String message = "I AM YOUR FATHER";
        final int packetId = 42;

        masterNetwork.setNetworkListener((channel) -> {
            PacketOutputStream packet = new PacketOutputStream(packetId);
            packet.writeUTF(message);
            channel.send(packet);
        });

        slaveNetwork.setNetworkListener((channel) -> {
            PacketInputStream packet = channel.receive();
            String receivedMsg = packet.readUTF();

            assertEquals(packetId, packet.getPacketType());
            assertEquals(message, receivedMsg);
        });

        masterNetwork.connect(slaveDevice);

        masterNetwork.testChannelThread();
        slaveNetwork.testChannelThread();
    }
}
