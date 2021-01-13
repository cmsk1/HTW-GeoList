package com.htwberlin.geolist.net.helper;

import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.data.interfaces.TaskListRepository;
import com.htwberlin.geolist.data.interfaces.UserRepository;
import com.htwberlin.geolist.data.models.TaskList;
import com.htwberlin.geolist.logic.GeoListLogic;
import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.INetwork;
import com.htwberlin.geolist.net.p2p.INetworkListener;
import com.htwberlin.geolist.net.protocol.GeoListProtocolEngine;
import com.htwberlin.geolist.net.protocol.IProtocol;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetInterface implements INetworkListener {
    //Important Note: Has to be single threaded in cause of the synchronized connections
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final INetwork network;
    private final GeoListProtocolEngine protocolEngine;
    private final UserRepository userRepo;
    private final TaskListRepository taskRepo;
    private AtomicBoolean doesActiveSync = new AtomicBoolean(false);
    private INetworkListener listenerForward;

    public NetInterface(INetwork network, GeoListProtocolEngine protocolEngine, DataStorage storage) {
        this.network = network;
        this.network.setNetworkListener(this);
        this.protocolEngine = protocolEngine;
        this.userRepo = storage.getUserRepo();
        this.taskRepo = storage.getTaskRepo();
    }

    public void scheduleSync() {
        this.executor.execute(this::doDeviceDiscovery);
    }

    public void scheduleShare(Device peerDevice, UUID listId) {
        ShareJob shareJob = new ShareJob(peerDevice, listId);
        this.executor.execute(shareJob);
    }

    private void doDeviceDiscovery() {
        if (!doesActiveSync.get()) {
            doesActiveSync.set(true);

            this.executor.submit(() -> {
                this.queueSyncableDevices();
                this.executor.execute(() -> doesActiveSync.set(false));
            });
        }
    }

    private void queueSyncableDevices() {
        Collection<Device> devices = this.network.getDevices();

        for (Device device : devices) {
            if (this.userRepo.isKnownUser(device.getSignature())) {
                SyncJob syncJob = new SyncJob(this.userRepo, device);
                this.executor.execute(syncJob);
            }
        }
    }

    public void acquireNetwork(Device device, INetworkListener listener) {
        this.listenerForward = listener;
        this.network.connect(device);
    }

    @Override
    public void handleConnection(IChannel channel) throws IOException {
        if (this.listenerForward != null) {
            this.forwardNetwork(channel);
        } else {
            IProtocol protocol = this.protocolEngine.slave();
            protocol.handleConnection(channel);
        }
    }

    private void forwardNetwork(IChannel channel) throws IOException {
        try {
            this.listenerForward.handleConnection(channel);
        } finally {
            this.listenerForward = null;
        }
    }

    public INetwork getNetwork() {
        return this.network;
    }

    public GeoListProtocolEngine getProtocolEngine() {
        return this.protocolEngine;
    }
}
