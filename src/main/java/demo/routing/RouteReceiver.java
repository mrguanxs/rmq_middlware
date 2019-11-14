package demo.routing;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import connection.RmqConnection;

import java.io.IOException;

/**
 * @author Mr.Guan
 * @since 2019/11/13
 * 订阅
 */
public class RouteReceiver {

    public static void main(String[] args) throws IOException {
        Connection consumerCon = RmqConnection.getConsumerConnection();
        Channel channel = consumerCon.createChannel();

        channel.exchangeDeclare(RouteSender.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, RouteSender.EXCHANGE_NAME, RouteSender.ROUTE);
        channel.queueBind(queueName, RouteSender.EXCHANGE_NAME, "debug");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" 我订阅的是 route:" +
                        delivery.getEnvelope().getRoutingKey() + "   消息:" + message);
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        System.out.println("订阅" + RouteSender.ROUTE);
    }
}
