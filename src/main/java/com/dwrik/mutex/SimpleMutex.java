package com.dwrik.mutex;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class SimpleMutex {
    private final AtomicInteger state;
    private final AtomicInteger listLock;
    private boolean hasWaiters;
    private Waiter head;

    public SimpleMutex() {
        state = new AtomicInteger(0);
        listLock = new AtomicInteger(0);
    }

    public void lock() {
        while (true) {
            if (state.compareAndSet(0, 1)) {
                return;
            } else {
                while (!listLock.compareAndSet(0, 1)) {
                    Thread.onSpinWait();
                };
                pushWaiter();
                hasWaiters = true;
                listLock.compareAndSet(1, 0);
                LockSupport.park();
            }
        }
    }

    public void unlock() {
        if (!hasWaiters) {
            state.compareAndSet(1, 0);
        } else {
            while (!listLock.compareAndSet(0, 1)) {
                Thread.onSpinWait();
            };
            Waiter waiter = popWaiter();
            hasWaiters = head != null;
            listLock.compareAndSet(1, 0);
            state.compareAndSet(1, 0);
            LockSupport.unpark(waiter.thread);
        }
    }

    private void pushWaiter() {
        if (head == null) {
            head = new Waiter(Thread.currentThread());
        } else {
            head = new Waiter(Thread.currentThread(), head);
        }
    }

    private Waiter popWaiter() {
        var current = head;
        head = head.next;
        return current;
    }
}
