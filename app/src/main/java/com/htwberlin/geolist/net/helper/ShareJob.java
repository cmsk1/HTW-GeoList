package com.htwberlin.geolist.net.helper;

import com.htwberlin.geolist.logic.GeoListLogic;
import com.htwberlin.geolist.net.p2p.IChannel;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.INetworkListener;
import com.htwberlin.geolist.net.protocol.IProtocol;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class ShareJob implements INetworkListener, Runnable {
    private final NetInterface net;
    private final Device peerDevice;
    private final UUID listId;
    private final CountDownLatch endLatch;

    public ShareJob(Device peerDevice, UUID listId) {
        this.net = GeoListLogic.getNetInterface();
        this.peerDevice = peerDevice;
        this.listId = listId;
        this.endLatch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            this.net.acquireNetwork(this.peerDevice, this);
            this.endLatch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // noop
        }
    }

    @Override
    public void handleConnection(IChannel channel) throws IOException {
        IProtocol shareProtocol = this.net.getProtocolEngine().share(this.listId);

        try {
            shareProtocol.handleConnection(channel);
        } finally {
            this.endLatch.countDown();
        }
    }
}
