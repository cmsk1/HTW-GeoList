package com.htwberlin.geolist.net.p2p.tcp;

import java.util.concurrent.atomic.AtomicBoolean;

class TcpAliveThread implements Runnable {
    private final ITimeoutListener listener;
    private final int timeout;
    private Thread thread;
    private AtomicBoolean stopRequested;

    public TcpAliveThread(ITimeoutListener listener, int timeout) {
        this.listener = listener;
        this.timeout = timeout;
    }

    public void start() {
        this.stopRequested = new AtomicBoolean(false);
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void stop() {
        this.stopRequested.set(true);
        this.thread.interrupt();
    }

    public void reset() {
        this.thread.interrupt();
    }

    @Override
    public void run() {
        while (!this.stopRequested.get()) {
            try {
                Thread.sleep(this.timeout);
                this.listener.onTimeout();
                break;
            } catch (InterruptedException e) {
                // noop
            }
        }
    }
}
