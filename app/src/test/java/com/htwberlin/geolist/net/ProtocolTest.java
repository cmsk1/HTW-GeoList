package com.htwberlin.geolist.net;

import com.htwberlin.geolist.data.NullDataStorage;
import com.htwberlin.geolist.data.NullTaskListRepository;
import com.htwberlin.geolist.data.NullUserRepository;
import com.htwberlin.geolist.logic.NullGeoListLogic;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.tcp.TcpMasterChannel;
import com.htwberlin.geolist.net.p2p.tcp.TcpSlaveChannel;
import com.htwberlin.geolist.net.packet.PacketType;
import com.htwberlin.geolist.utils.AssertThread;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.UUID;

public class ProtocolTest {
    private static final int TEST_PORT = 44444;
    private static final int MASTER_SLAVE_DELAY = 500;
    private static final int TIMEOUT = 2000;
    private static Device masterDevice;
    private static Device slaveDevice;
    private NullTaskListRepository taskRepo;
    private NullUserRepository userRepo;
    private NullGeoListProtocolEngine defaultEngine;
    private TcpMasterChannel masterChannel;
    private TcpSlaveChannel slaveChannel;

    @BeforeAll
    public static void beforeAll() {
        masterDevice = new Device("Master", "master-83659261");
        slaveDevice = new Device("Slave", "slave-09385741");
    }

    @BeforeEach
    public void beforeEach() {
        NullDataStorage storage = new NullDataStorage();
        this.taskRepo = storage.getTaskRepo();
        this.userRepo = storage.getUserRepo();
        this.defaultEngine = new NullGeoListProtocolEngine(storage);

        this.masterChannel = new TcpMasterChannel(masterDevice, TEST_PORT);
        this.slaveChannel = new TcpSlaveChannel(slaveDevice, InetAddress.getLoopbackAddress(), TEST_PORT);

        this.masterChannel.setTimeout(TIMEOUT);
        this.slaveChannel.setTimeout(TIMEOUT);

        NullGeoListLogic.makeInstance(null);
    }

    @Test
    public void syncWithUnknownDeviceWillBeDenied() {
        SpyGeoListProtocol master = defaultEngine.sync();
        SpyGeoListProtocol slave = defaultEngine.slave();

        AssertThread masterRun = new AssertThread(() -> {
            masterChannel.open();
            master.handleConnection(masterChannel);
            masterChannel.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slaveChannel.open();
            slave.handleConnection(slaveChannel);
            slaveChannel.disconnect();
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();

        assertEquals(1, master.getReceivedCount());
        assertEquals(1, slave.getReceivedCount());

        slave.assertReceived(PacketType.SNC);
        master.assertReceived(PacketType.DNY);
    }

    @Test
    public void syncWithKnownDeviceWillBeAccepted() {
        SpyGeoListProtocol master = defaultEngine.sync();
        SpyGeoListProtocol slave = defaultEngine.slave();
        this.userRepo.addUser(slaveDevice.getSignature(), slaveDevice.getDisplayName());
        this.userRepo.addUser(masterDevice.getSignature(), masterDevice.getDisplayName());

        AssertThread masterRun = new AssertThread(() -> {
            masterChannel.open();
            master.handleConnection(masterChannel);
            masterChannel.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slaveChannel.open();
            slave.handleConnection(slaveChannel);
            slaveChannel.disconnect();
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();

        slave.assertReceived(PacketType.SNC);
        master.assertReceived(PacketType.SGN);
    }

    @Test
    public void shareWithDeviceCanBeDenied() {
        NullGeoListLogic.instance().setSharePermitted(false);
        UUID tasklistId = UUID.randomUUID();
        taskRepo.addList("Test List", tasklistId);
        SpyGeoListProtocol master = defaultEngine.share(tasklistId);
        SpyGeoListProtocol slave = defaultEngine.slave();

        AssertThread masterRun = new AssertThread(() -> {
            masterChannel.open();
            master.handleConnection(masterChannel);
            masterChannel.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slaveChannel.open();
            slave.handleConnection(slaveChannel);
            slaveChannel.disconnect();
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();

        assertEquals(1, master.getReceivedCount());
        assertEquals(1, slave.getReceivedCount());

        slave.assertReceived(PacketType.SHR);
        master.assertReceived(PacketType.DNY);
    }

    @Test
    public void shareWithDeviceCanBeAccepted() {
        NullGeoListLogic.instance().setSharePermitted(true);
        UUID tasklistId = UUID.randomUUID();
        taskRepo.addList("Test List", tasklistId);
        SpyGeoListProtocol master = defaultEngine.share(tasklistId);
        SpyGeoListProtocol slave = defaultEngine.slave();

        AssertThread masterRun = new AssertThread(() -> {
            masterChannel.open();
            master.handleConnection(masterChannel);
            masterChannel.disconnect();
        });

        AssertThread slaveRun = new AssertThread(() -> {
            slaveChannel.open();
            slave.handleConnection(slaveChannel);
            slaveChannel.disconnect();
        });

        masterRun.start();
        AssertThread.sleep(MASTER_SLAVE_DELAY);
        slaveRun.start();

        masterRun.test();
        slaveRun.test();

        slave.assertReceived(PacketType.SHR);
        master.assertReceived(PacketType.SGN);
    }
}
