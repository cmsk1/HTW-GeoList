package com.htwberlin.geolist.net.protocol;

import java.io.IOException;

public class IllegalProtocolState extends IOException {
    public IllegalProtocolState(int expected, int actual) {
        super("An illegal protocol state occurred. Expected [" + expected + "] but received [" + actual + "]");
    }

    public IllegalProtocolState(int type) {
        super("An illegal protocol state occurred. Received unexpected [" + type + "] packet");
    }
}
