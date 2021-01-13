package com.htwberlin.geolist;

import java.io.Closeable;
import java.io.IOException;

public final class Utils {
    public static void closeSafe(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // noop
        }
    }

    public static void sleepSafe(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // noop
        }
    }
}
