package com.htwberlin.geolist.net.p2p;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractChannel implements IChannel {
    protected final List<IChannelListener> listeners = new ArrayList<>();

    protected void fireStateChange(ConnectionState state) {
        for (IChannelListener listener : this.listeners) {
            listener.onStateChanged(state);
        }
    }

    public void addListener(IChannelListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(IChannelListener listener) {
        this.listeners.remove(listener);
    }
}
