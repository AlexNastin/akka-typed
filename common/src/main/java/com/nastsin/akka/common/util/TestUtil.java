package com.nastsin.akka.common.util;

import java.util.concurrent.atomic.AtomicInteger;

public class TestUtil {

    private static final AtomicInteger atomicInteger = new AtomicInteger();

    public static int getId() {
        return atomicInteger.getAndIncrement();
    }
}