package com.htwberlin.geolist.net.p2p;

public class Device {
    private final String signature;
    private final String displayName;

    public Device(String signature, String displayName) {
        this.signature = signature;
        this.displayName = displayName;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Device) {
            Device other = (Device)o;
            return this.signature.equals(other.signature);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.signature.hashCode();
    }

    @Override
    public String toString() {
        return "Device{" + this.displayName + "@" + this.signature + "}";
    }
}
