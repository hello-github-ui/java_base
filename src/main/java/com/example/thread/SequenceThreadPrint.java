package com.example.thread;

import java.util.concurrent.*;

/**
 * Created by 19921224 on 2023/9/18 14:21
 * 第一题:
 * 三个线程分别打印 A，B，C，要求这三个线程一起运行，打印 n 次，输出形如“ABCABCABC....”的字符串
 */
public class SequenceThreadPrint {
    public static void main(String[] args) {
        // ExecutorService executor = Executors.newFixedThreadPool(5);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                8,  // corePoolSize：线程池的核心线程数，最初创建的线程数。
                30, // maximumPoolSize：线程池的最大线程数，可以创建的最大线程数。
                10,// keepAliveTime：空闲线程的最大存活时间，超过这个时间，多余的线程会被终止。
                TimeUnit.SECONDS, // 时间单位，用于指定keepAliveTime的单位。
                new ArrayBlockingQueue<Runnable>(9), // 任务队列，用于存储等待执行的任务，这里使用了一个有界队列，最多可以存储9个任务。
                Executors.defaultThreadFactory(), // 线程工厂，用于创建线程。
                new RejectedExecutionHandler() { // 拒绝策略，当任务无法被执行时，执行的处理器。
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println("\n " + r.toString() + " is rejected... ");
                    }
                }
        );

        int n = 10;
        PrintABC abc = new PrintABC(n);

        Thread threadA = new Thread(() -> {
            try {
                abc.printA();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                abc.printB();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread threadC = new Thread(() -> {
            try {
                abc.printC();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        for (int i = 0; i < 10; i++) {
            poolExecutor.submit(threadA);
            poolExecutor.submit(threadB);
            poolExecutor.submit(threadC);
        }

        poolExecutor.shutdown();
    }
}


class PrintABC {
    // 打印次数
    private int n;
    private int count = 1;

    public PrintABC(int n) {
        this.n = n;
    }

    synchronized public void printA() throws InterruptedException {
        while (count <= n) {
            if (count % 3 == 1) {
                System.out.print("A");
                count++;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    synchronized public void printB() throws InterruptedException {
        while (count <= n) {
            if (count % 3 == 2) {
                System.out.print("B");
                count++;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    synchronized public void printC() throws InterruptedException {
        while (count <= n) {
            if (count % 3 == 0) {
                System.out.print("C");
                count++;
                notifyAll();
            } else {
                wait();
            }
        }
    }
}