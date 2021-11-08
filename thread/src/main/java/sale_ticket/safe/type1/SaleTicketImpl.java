package sale_ticket.safe.type1;

/**
 * @author 030
 * @date 14:57 2021/11/4
 * @description 解决线程安全问题方式一：使用 synchronized 同步代码块
 */
public class SaleTicketImpl implements Runnable{
    /*定义一个多线程共享的资源*/
    private int ticket = 100;

    // 解决线程安全问题的第一种方案：使用同步代码块
    /**
     * 格式：
     *  synchronized(锁对象){
     *      // 这里写可能会出现线程安全问题的代码（访问了共享数据的代码）
     *  }
     *
     *  注意：
     *      1. 同步代码块中的锁对象，可以使用任意的对象。
     *      2. 但是必须保证多个线程使用的锁对象是同一个
     *      3. 锁对象作用：把同步代码块锁住，只让一个线程在同步代码块中执行。
     */
    // 构造 Object 锁对象
    Object obj = new Object();

    /*实现多线程任务：卖票*/
    @Override
    public void run() {
        // 使用死循环，重复卖票
        while (true) {
            synchronized (obj) {
                if (ticket > 0) {
                    try {
                        // 为了提高安全问题出现的概率， 让程序在这里睡眠一会
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 票存在，卖票，ticket--
                    System.out.println(Thread.currentThread().getName() + "正在出售第" + ticket + "张票");
                    ticket--;
                }
            }
        }
    }
}
