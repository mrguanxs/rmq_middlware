package demo.pub_sub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import config.ExchangeType;
import connection.RmqConnection;

import java.io.IOException;

/**
 * @author Mr.Guan
 * @since 2019/11/13
 * 广播
 */
public class Pub {
    protected static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException {

        Connection producerCon = RmqConnection.getProducerConnection();
        Channel channel = producerCon.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, ExchangeType.FANOUT);
        String msg = "这是一个广播，你能收到吗";
        channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes("UTF-8"));
        System.out.println("广播消息：" + msg);

    }
}
