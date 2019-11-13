package pub_sub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import config.ExchangeType;
import connection.RmqConnection;

import java.io.IOException;

/**
 * @author Mr.Guan
 * @since 2019/11/13
 */
public class Sub {

    public static void main(String[] args) throws IOException {
        sub();
        sub();
    }

    public static void sub() throws IOException {
        Connection consumerCon = RmqConnection.getConsumerConnection();

        Channel channel = consumerCon.createChannel();
        channel.exchangeDeclare(Pub.EXCHANGE_NAME, ExchangeType.FANOUT);
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, Pub.EXCHANGE_NAME, "");

        System.out.println("等待接收广播。。。。。。。");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("接收到广播：" + message );
        };

        channel.basicConsume(queue,true, deliverCallback, consumerTag -> { });
        System.out.println("接收广播结束==========");
    }
}
