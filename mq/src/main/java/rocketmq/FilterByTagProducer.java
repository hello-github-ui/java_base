package rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @author 030
 * @date 20:22 2021/10/29
 * @description 定义Tag过滤Producer
 */
public class FilterByTagProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("pg");
        producer.setNamesrvAddr("rocketmqOS:9876");
        producer.start();
        String[] tags = {"myTagA", "myTagB", "myTagC"};
        for (int i = 0; i < 10; i++) {
            byte[] body = ("Hi," + i).getBytes();
            String tag = tags[i % tags.length];
            Message msg = new Message("myTopic", tag, body);
            SendResult sendResult = producer.send(msg);
            System.out.println(sendResult);
        }
        producer.shutdown();
    }
}
