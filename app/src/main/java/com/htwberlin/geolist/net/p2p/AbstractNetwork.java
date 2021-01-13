package com.htwberlin.geolist.net.p2p;

import com.htwberlin.geolist.net.IPeersListener;

import java.util.Collection;

public abstract class AbstractNetwork implements INetwork {
    private IPeersListener peersListener;
    private INetworkListener netListener;

    protected void openChannel(IChannel channel) {
        try {
            channel.open();
            if (this.netListener == null) return;
            this.netListener.handleConnection(channel);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            channel.disconnect();
        }
    }

    public void firePeersDiscovered(Collection<Device> devices) {
        if (this.peersListener != null) {
            this.peersListener.onPeersDiscovered(devices);
        }
    }

    @Override
    public void setNetworkListener(INetworkListener listener) {
        this.netListener = listener;
    }

    @Override
    public void setPeersListener(IPeersListener listener) {
        this.peersListener = listener;
    }
}
