package com.deange.wkrpt300.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Countdown {

    private final AtomicInteger mCountdown;

    public Countdown() {
        mCountdown = new AtomicInteger(0);
    }

    public void signal() {
        mCountdown.decrementAndGet();
    }

    public void await() {
        mCountdown.incrementAndGet();
    }

    public boolean isDone() {
        return mCountdown.get() == 0;
    }

    public void blockUntilDone() {
        while (true) if (isDone()) break;
    }
}
