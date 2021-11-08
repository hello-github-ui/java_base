package sale_ticket.unsafe;

/**
 * @author 030
 * @date 14:58 2021/11/4
 * @description 模拟卖票案例：创建3个线程，同时开启，对共享票进行出售
 */
public class SaleTicketTest {
    public static void main(String[] args) {
        // 创建Runnable接口的实现类对象

        /*线程不安全的售票类*/
        SaleTicketImpl saleImpl = new SaleTicketImpl();
        try {
            // 因为 Runnable 接口中没有提供 start开启线程方法，因此还需要通过 Thread 类实例对象来实现。
            // 创建Thread类对象，构造方法中传递Runnable接口的实现类。
            Thread t1 = new Thread(saleImpl);
            t1.setName("一号窗口");
            Thread t2 = new Thread(saleImpl);
            t2.setName("二号窗口");
            Thread t3 = new Thread(saleImpl);
            t3.setName("三号窗口");

            // 调用 start 方法开启多线程
            t1.start();
            t2.start();
            t3.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
