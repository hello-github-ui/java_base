package sale_ticket.unsafe;

/**
 * @author 030
 * @date 14:45 2021/11/4
 * @description 实现卖票案例
 */
public class SaleTicketImpl implements Runnable {
    /*定义一个多线程共享的资源*/
    private int ticket = 100;

    /*实现多线程任务：卖票*/
    @Override
    public void run() {
        // 使用死循环，重复卖票
        while (true) {
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
