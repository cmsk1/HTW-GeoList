package com.htwberlin.geolist.net.protocol;

import java.io.IOException;

@FunctionalInterface
public interface IProtocolState {
    IProtocolState transition() throws IllegalProtocolState, IOException;
}
