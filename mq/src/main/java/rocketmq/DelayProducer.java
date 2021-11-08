package rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 030
 * @date 19:19 2021/10/29
 * @description 定义DelayProducer类
 */
public class DelayProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("pg");
        producer.setNamesrvAddr("rocketmqOS:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            byte[] body = ("Hi," + i).getBytes();
            Message msg = new Message("TopicB", "someTag", body);
            // 指定消息延迟等级为3级，即延迟10s
            // msg.setDelayTimeLevel(3);
            SendResult sendResult = producer.send(msg);
            // 输出消息被发送的时间
            System.out.print(new SimpleDateFormat("mm:ss").format(new
                Date()));
            System.out.println(" ," + sendResult);
        }
        producer.shutdown();
    }
}
