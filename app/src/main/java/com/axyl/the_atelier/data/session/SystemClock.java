package com.axyl.the_atelier.data.session;

/** Production implementation of {@link Clock} that delegates to {@link System#currentTimeMillis()}. */
public final class SystemClock implements Clock {
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
