package com.htwberlin.geolist.net.helper;

import com.htwberlin.geolist.data.interfaces.UserRepository;
import com.htwberlin.geolist.data.models.User;
import com.htwberlin.geolist.logic.GeoListLogic;
import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.INetwork;
import com.htwberlin.geolist.net.p2p.INetworkListener;
import com.htwberlin.geolist.net.protocol.GeoListProtocol;
import com.htwberlin.geolist.net.protocol.GeoListProtocolEngine;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class SyncJob implements INetworkListener, Runnable {
    private final NetInterface net;
    private final UserRepository userRepo;
    private final Device device;
    private final CountDownLatch endLatch;

    public SyncJob(UserRepository userRepo, Device device) {
        this.net = GeoListLogic.getNetInterface();
        this.userRepo = userRepo;
        this.device = device;
        this.endLatch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            this.net.acquireNetwork(this.device, this);
            this.endLatch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // noop
        }
    }

    @Override
    public void handleConnection(IChannel channel) throws IOException {
        GeoListProtocol protocol = this.net.getProtocolEngine().sync();

        try {
            protocol.handleConnection(channel);
            User user = userRepo.getUser(this.device.getSignature());

            if (user != null) {
                user.updateLastSync();
            }
        } finally {
            this.endLatch.countDown();
        }
    }
}
