package rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

/**
 * @author 030
 * @date 18:39 2021/10/29
 * @description 定义单向消息发送生产者
 */
public class OnewayProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("pg");
        producer.setNamesrvAddr("rocketmqOS:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            byte[] body = ("Hi," + i).getBytes();
            Message msg = new Message("single", "someTag", body);
            // 单向发送
            producer.sendOneway(msg);
        }
        producer.shutdown();
        System.out.println("producer shutdown");
    }
}
