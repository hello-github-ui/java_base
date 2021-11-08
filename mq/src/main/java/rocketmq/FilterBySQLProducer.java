package rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @author 030
 * @date 20:25 2021/10/29
 * @description 定义SQL过滤Producer
 */
public class FilterBySQLProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("pg");
        producer.setNamesrvAddr("rocketmqOS:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            try {
                byte[] body = ("Hi," + i).getBytes();
                Message msg = new Message("myTopic", "myTag", body);
                msg.putUserProperty("age", i + "");
                SendResult sendResult = producer.send(msg);
                System.out.println(sendResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        producer.shutdown();
    }
}
