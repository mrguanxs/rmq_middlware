package demo.routing;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import connection.RmqConnection;

import java.io.IOException;
import java.util.Random;

/**
 * @author Mr.Guan
 * @since 2019/11/13
 * 发布
 */
public class RouteSender {

    protected static final String EXCHANGE_NAME = "direct_logs";
    protected static final String ROUTE = "debug";

    public static void main(String[] args) throws IOException {
        Connection producerCon = RmqConnection.getProducerConnection();
        Channel channel = producerCon.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String msg = "你订阅的是" + ROUTE + new Random().nextInt();

        channel.basicPublish(EXCHANGE_NAME, ROUTE, null, msg.getBytes("UTF-8"));
        System.out.println("发布订阅");
    }
}
