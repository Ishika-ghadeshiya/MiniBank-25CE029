
package service;

import model.Account;
import exception.BankException;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TransferCoordinator {

    public static void demonstrateDeadlock() {

        Object firstLock = new Object();
        Object secondLock = new Object();

        CountDownLatch bothHoldingFirst = new CountDownLatch(2);

        Runnable taskOne = () -> {
            synchronized (firstLock) {
                bothHoldingFirst.countDown();
                awaitLatch(bothHoldingFirst);

                synchronized (secondLock) {
                    System.out.println("First transfer completed.");
                }
            }
        };

        Runnable taskTwo = () -> {
            synchronized (secondLock) {
                bothHoldingFirst.countDown();
                awaitLatch(bothHoldingFirst);

                synchronized (firstLock) {
                    System.out.println("Second transfer completed.");
                }
            }
        };

        Thread first = new Thread(taskOne, "Deadlock-A");
        Thread second = new Thread(taskTwo, "Deadlock-B");

        first.setDaemon(true);
        second.setDaemon(true);

        first.start();
        second.start();

        ThreadMXBean bean =
                ManagementFactory.getThreadMXBean();

        boolean detected = false;

        for (int i = 0; i < 50; i++) {
            long[] ids = bean.findMonitorDeadlockedThreads();

            if (ids != null && ids.length >= 2) {
                detected = true;
                break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Deadlock Detected: " + detected);
    }

    private static void awaitLatch(CountDownLatch latch) {
        boolean interrupted = false;

        while (true) {
            try {
                latch.await();
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public static void safeTransfer(
            Account from, Account to, long amount)
            throws BankException {

        if (from == null || to == null) {
            throw new IllegalArgumentException(
                "Accounts cannot be null."
            );
        }

        if (from == to) {
            throw new IllegalArgumentException(
                "Source and destination must differ."
            );
        }

        Account first = from.getAccountNumber()
                .compareTo(to.getAccountNumber()) < 0
                ? from : to;

        Account second = first == from ? to : from;

        synchronized (first) {
            synchronized (second) {
                from.transfer(to, amount);
            }
        }
    }
}

