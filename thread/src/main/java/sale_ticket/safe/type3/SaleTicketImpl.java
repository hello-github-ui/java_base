package sale_ticket.safe.type3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 030
 * @date 15:21 2021/11/4
 * @description 解决线程安全问题方式三：使用 Lock锁（jdk1.5之后才有的接口，常见的该接口实现类 ReentrantLock）
 * 使用步骤：
 *  1. 在成员位置创建一个 ReentrantLock 对象
 *  2. 在可能会出现线程安全问题的代码前调用 Lock 接口中的 lock() 方法获取锁
 *  3. 在可能会出现线程安全问题的代买后调用 Lock 接口中的 unLock() 方法释放锁
 */
public class SaleTicketImpl implements Runnable{
    /*定义一个多线程共享的资源*/
    private int ticket = 100;

    /*1. 在成员位置创建一个 ReentrantLock 对象*/
    Lock lock = new ReentrantLock();

    /*实现多线程任务：卖票*/
    @Override
    public void run() {
        // 使用死循环，重复卖票
        while (true) {
            /*2. 在可能会出现线程安全问题的代码前调用 Lock 接口中的 lock() 方法获取锁*/
            lock.lock();
            if (ticket > 0) {
                try {
                    // 为了提高安全问题出现的概率， 让程序在这里睡眠一会
                    Thread.sleep(10);
                    // 票存在，卖票，ticket--
                    System.out.println(Thread.currentThread().getName() + "正在出售第" + ticket + "张票");
                    ticket--;
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    /*3. 在可能会出现线程安全问题的代买后调用 Lock 接口中的 unLock() 方法释放锁*/
                    lock.unlock();
                }

            }
        }
    }
}
