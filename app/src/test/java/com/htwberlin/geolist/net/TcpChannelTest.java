package com.htwberlin.geolist.net;

import com.htwberlin.geolist.logic.NullGeoListLogic;
import com.htwberlin.geolist.net.p2p.ConnectionState;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.tcp.TcpChannel;
import com.htwberlin.geolist.net.p2p.tcp.TcpMasterChannel;
import com.htwberlin.geolist.net.p2p.tcp.TcpSlaveChannel;
import com.htwberlin.geolist.net.packet.PacketInputStream;
import com.htwberlin.geolist.net.packet.PacketOutputStream;
import com.htwberlin.geolist.net.packet.PacketType;
import com.htwberlin.geolist.utils.AssertThread;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpChannelTest {
    private static final int TEST_PORT = 44444;
    private static final int MASTER_SLAVE_DELAY = 500;
    private static final int TIMEOUT = 2000;
    private static final int TIME_DELTA = 1000;

    private TcpChannel master;
    private TcpChannel slave;

    private static Device masterDevice;
    private static Device slaveDevice;

    @BeforeAll
    public static void beforeAll() {
        masterDevice = new Device("Master", "master-83659261");
        slaveDevice = new Device("Slave", "slave-09385741");
    }

    @BeforeEach
    public void beforeEach() {
        this.master = new TcpMasterChannel(masterDevice, TEST_PORT);
        this.slave = new TcpSlaveChannel(slaveDevice, InetAddress.getLoopbackAddress(), TEST_PORT);

        this.master.setTimeout(TIMEOUT);
        this.slave.setTimeout(TIMEOUT);
    }

    @Test
    public void connectionBetweenChannelsEstablished() {
        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            master.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            slave.disconnect();
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();
    }

    @Test
    public void channelGetsClosedOnDisconnect() {
        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            master.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            assertThrows(EOFException.class, () -> slave.receive());
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();
    }

    @Test
    public void channelGetsClosedOnUnableToConnect() {
        AssertThread masterRun = new AssertThread(() -> assertThrows(IOException.class, () -> master.open()));

        masterRun.start();
        masterRun.test();
    }

    @Test
    public void channelGetsClosedOnMessageTimeout() {
        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            Thread.sleep(TIMEOUT + TIME_DELTA);
            master.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            assertThrows(IOException.class, () -> slave.receive());
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();
    }

    @Test
    public void sendMessageFromMasterToSlave() {
        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            byte[] message = new byte[] { 1, 2, 3 };
            master.sendRaw(message);
            master.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            byte[] message = slave.receiveRaw();
            slave.disconnect();

            assertArrayEquals(new byte[] { 1, 2, 3 }, message);
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();
    }

    @Test
    public void sendMessageFromSlaveToMaster() {
        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            byte[] message = master.receiveRaw();
            master.disconnect();

            assertArrayEquals(new byte[] { 1, 2, 3 }, message);
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            byte[] message = new byte[] { 1, 2, 3 };
            slave.sendRaw(message);
            slave.disconnect();
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();
    }

    @Test
    public void sendMultipleMessagesBetweenChannels() {
        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            master.sendRaw("Hallo Slave".getBytes());
            String fromSlave1 = new String(master.receiveRaw());
            String fromSlave2 = new String(master.receiveRaw());
            master.sendRaw("Bye Slave".getBytes());
            master.disconnect();

            assertEquals(fromSlave1, "Hallo Master");
            assertEquals(fromSlave2, "Bye Master");
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            String fromMaster1 = new String(slave.receiveRaw());
            slave.sendRaw("Hallo Master".getBytes());
            slave.sendRaw("Bye Master".getBytes());
            String fromMaster2 = new String(slave.receiveRaw());
            slave.disconnect();

            assertEquals(fromMaster1, "Hallo Slave");
            assertEquals(fromMaster2, "Bye Slave");
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();
    }

    @Test
    public void sendPacketsBetweenChannels() {
        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            PacketOutputStream packet = new PacketOutputStream(PacketType.CMP);
            packet.writeUTF("Hallo Slave");
            packet.writeInt(3001);
            master.send(packet);
            master.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            PacketInputStream packet = slave.receive();
            String msg = packet.readUTF();
            int code = packet.readInt();
            slave.disconnect();

            assertEquals("Hallo Slave", msg);
            assertEquals(3001, code);
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();
    }

    @Test
    public void channelIOCantBeUsedBeforeConnection() {
        assertThrows(IOException.class, () -> this.master.sendRaw(new byte[0]));
        assertThrows(IOException.class, () -> this.master.receiveRaw());
    }

    @Test
    public void channelConnectedGetsFired() {
        final AtomicBoolean eventFiredMaster = new AtomicBoolean(false);
        final AtomicBoolean eventFiredSlave = new AtomicBoolean(false);

        master.addListener((ConnectionState state) -> {
            if (state == ConnectionState.CONNECTED) {
                eventFiredMaster.set(true);
            }
        });

        slave.addListener((ConnectionState state) -> {
            if (state == ConnectionState.CONNECTED) {
                eventFiredSlave.set(true);
            }
        });

        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            master.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            slave.disconnect();
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();

        assertTrue(eventFiredMaster.get());
        assertTrue(eventFiredSlave.get());
    }

    @Test
    public void channelUnableToConnectGetsFired() {
        final AtomicBoolean eventFired = new AtomicBoolean(false);

        master.addListener((ConnectionState state) -> {
            if (state == ConnectionState.UNABLE_TO_CONNECT) {
                eventFired.set(true);
            }
        });

        AssertThread masterRun = new AssertThread(() -> {
            master.open();
        }, true);

        masterRun.start();
        masterRun.test();

        assertTrue(eventFired.get());
    }

    @Test
    public void channelTimeoutGetsFired() {
        final AtomicBoolean eventFired = new AtomicBoolean(false);

        slave.addListener((ConnectionState state) -> {
            if (state == ConnectionState.MESSAGE_TIMEOUT) {
                eventFired.set(true);
            }
        });

        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            Thread.sleep(TIMEOUT + TIME_DELTA);
            master.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            slave.receive();
        }, true);

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();

        assertTrue(eventFired.get());
    }

    @Test
    public void channelIOFailureGetsFired() {
        final AtomicBoolean eventFired = new AtomicBoolean(false);

        slave.addListener((ConnectionState state) -> {
            if (state == ConnectionState.IO_FAILED) {
                eventFired.set(true);
            }
        });

        AssertThread masterRun = new AssertThread(() -> {
            master.open();
            master.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slave.open();
            slave.receive();
        }, true);

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();

        assertTrue(eventFired.get());
    }
}
