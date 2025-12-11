package com.dwrik.mutex;

public class Main {
    private static final int THREAD_COUNT = 10;
    private static final int ITERATIONS = 10_000;
    private static int counter = 0;

    public static void main(String[] args) {
        var threads = new Thread[THREAD_COUNT];
        var mutex = new SimpleMutex();
        long start = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < ITERATIONS; j++) {
                    mutex.lock();
                    try {
                        Thread.sleep(1);
                    } catch (Exception ignored) {}
                    counter++;
                    mutex.unlock();
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.err.println("signal interrupt");
            }
        }

        long end = System.currentTimeMillis();
        System.out.printf("Counter after %d * %d iterations: %d\n", THREAD_COUNT, ITERATIONS, counter);
        System.out.printf("Time taken: %d ms\n", end - start);
    }
}
