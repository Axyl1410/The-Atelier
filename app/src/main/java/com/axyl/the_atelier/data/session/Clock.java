package com.axyl.the_atelier.data.session;

/**
 * Abstraction over {@link System#currentTimeMillis()} to allow time to be
 * controlled in unit tests.
 */
public interface Clock {
    long currentTimeMillis();
}
