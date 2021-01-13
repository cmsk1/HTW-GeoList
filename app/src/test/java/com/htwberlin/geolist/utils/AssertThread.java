package com.htwberlin.geolist.utils;

import org.junit.jupiter.api.Assertions;

public class AssertThread {
    private final Thread thread;
    private final boolean ignoreExceptions;
    private Throwable error;

    public AssertThread(final AssertRunnable runnable) {
        this(runnable, false);
    }

    public AssertThread(final AssertRunnable runnable, boolean ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;

        this.thread = new Thread(() -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                e.printStackTrace();
                this.error = e;
            }
        });
    }

    public void start() {
        this.thread.start();
    }

    public void test() {
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            // noop
        }
        if (this.error == null) return;
        this.rethrowException();
    }

    private void rethrowException() {
        if (!this.ignoreExceptions || (this.error instanceof AssertionError)) {
            if (this.error instanceof AssertionError) {
                throw (AssertionError)this.error;
            } else {
                Assertions.fail("an unexpected exception occurred");
            }
        }
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // noop
        }
    }

    @FunctionalInterface
    public interface AssertRunnable {
        void run() throws Throwable;
    }
}
