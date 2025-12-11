package com.dwrik.mutex;

public class Waiter {
    Thread thread;
    Waiter next;

    Waiter(Thread thread) {
        this.thread = thread;
    }

    Waiter(Thread thread, Waiter next) {
        this.thread = thread;
        this.next = next;
    }
}
