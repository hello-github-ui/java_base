package com.example.exec.thead_print;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <a>https://www.cnblogs.com/lazyegg/p/13900847.html</a>
 * 三个线程分别打印 A，B，C，要求这三个线程一起运行，打印 n 次，输出形如“ABCABCABC....”的字符串
 */
public class PrintABCUsingLock {

    private int time;   // 控制打印次数
    private int state;  // 当前状态值：保证三个线程之间交替打印
    private Lock lock = new ReentrantLock();

    private PrintABCUsingLock(int time) {
        this.time = time;
    }

    /**
     * 打印
     */
    private void printLetter(String name, int targetNum) {
        for (int i = 0; i < time; ) {
            lock.lock();
            if (state % 3 == targetNum) {
                state++;
                i++;
                System.out.println(name);
            }
            lock.unlock();
        }
    }

    /**
     * main 方法启动后，3个线程会抢锁，但是 state 的初始值为0，所以第一次执行 if 语句的内容只能是 线程A，然后还在 for 循环之内，此时 stage = 1，
     * 只有线程B 才满足 1%3==1，所以第二个执行的是 B， 同理只有线程 C 才满足 2%3==2，所以第三个执行的是 C，执行完 ABC 之后，采取执行第二次 for 循环，
     * 所以要把 i++ 写在 for 循环里面，不能写成 for(int i=0; i<times; i++) 这样
     */
    public static void main(String[] args) throws InterruptedException {
        PrintABCUsingLock abcUsingLock = new PrintABCUsingLock(1);

        // 构造一个大小为10个线程的 线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        // 通过线程池提交线程
        executorService.submit(new Thread(() -> {
            abcUsingLock.printLetter("B", 1);
        }, "B"));

        executorService.submit(new Thread(() -> {
            abcUsingLock.printLetter("A", 0);
        }, "A"));

        executorService.submit(new Thread(() -> {
            abcUsingLock.printLetter("C", 2);
        }, "C"));

        // 手动释放线程池
        executorService.shutdown();


        // 普通线程执行
//        new Thread(() -> {
//            abcUsingLock.printLetter("A", 0);
//        }, "A").start();

//        new Thread(() -> {
//            abcUsingLock.printLetter("C", 2);
//        }, "C").start();
    }
}
