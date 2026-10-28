
package service;

import java.util.concurrent.CountDownLatch;

public class UnsafeCounter {

    private int value = 0;

    public void increment() {
        int oldValue = value;
        Thread.yield();
        value = oldValue + 1;
    }

    public int getValue() {
        return value;
    }

    public static int runTest(int threadCount, int times)
            throws InterruptedException {

        UnsafeCounter counter = new UnsafeCounter();

        Thread[] threads = new Thread[threadCount];
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    start.await();

                    for (int j = 0; j < times; j++) {
                        counter.increment();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads[i].start();
        }

        start.countDown();

        for (Thread thread : threads) {
            thread.join();
        }

        return counter.getValue();
    }
}
