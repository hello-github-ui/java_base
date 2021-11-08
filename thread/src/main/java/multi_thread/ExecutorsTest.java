package multi_thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 030
 * @date 13:48 2021/11/8
 * @description
 *  Executors 是一个线程池工厂方法，用来构造一个线程池【注意：不是创建线程，而是线程池】，
 *  返回的就是一个ExecutorService接口的实现类。ExecutorService就是一个线程池接口
 */
public class ExecutorsTest {
    @Test
    public void test(){
        // 你可以使用 ExecutorService 接口来接收一个线程池对象，也可以使用 ExecutorService 的子类实现来接收
        // 也是一种 多态 的体现
        // 构造了一个固定数量为两个的线程池 对象
        ExecutorService es = Executors.newFixedThreadPool(2);
        // 通过 调用线程池对象的submit方法，将一个任务加入到线程池中，
        // 当线程池中有空闲线程后，就会自动执行任务
        es.submit(new RunnableImpl()); // pool-1-thread-1线程启动
        es.submit(new RunnableImpl()); // pool-1-thread-2线程启动
        es.submit(new RunnableImpl()); // pool-1-thread-1线程启动
        // 可以看到线程池中只有2个线程，但是我这里却构造了三个任务，所以任务并不会立即执行，还需等待有空闲线程才可被执行
    }

    class RunnableImpl implements Runnable{
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "线程启动");
        }
    }
}
