package helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author mrguanxs@163.com
 * @date 2019/5/15 17:30
 */
public class Receiver {
    private final static String QUEUE_NAME = "hello2";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("192.168.193.155");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setConnectionTimeout(600000); // in milliseconds
        factory.setRequestedHeartbeat(60); // in seconds
        factory.setHandshakeTimeout(6000); // in milliseconds
        factory.setRequestedChannelMax(5);
        factory.setNetworkRecoveryInterval(500);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("Waiting for messages. ");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

}
