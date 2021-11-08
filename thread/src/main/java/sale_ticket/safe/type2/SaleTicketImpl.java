package sale_ticket.safe.type2;

/**
 * @author 030
 * @date 15:03 2021/11/4
 * @description 解决线程安全问题方式二：使用 同步方法
 *  使用步骤：
 *      1. 把访问了共享数据的代码抽取出来，放到一个方法中
 *      2. 在方法上添加 synchronized 修饰符
 */
public class SaleTicketImpl implements Runnable{
    /*定义一个多线程共享的资源*/
    private int ticket = 100;

    /*实现多线程任务：卖票*/
    @Override
    public void run() {
        // 使用死循环，重复卖票
        while (true) {
            saleTicket();
        }
    }

    /**
     * 定义一个同步方法
     * 同步方法也会把方法内部的代码锁住，只让一个线程执行
     * 同步方法的锁对象是谁？
     *  就是实现类对象 new SaleTicketImpl()，也就是 This
     */
    public synchronized void saleTicket(){
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