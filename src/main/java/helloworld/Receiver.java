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
        //打开链接和通道
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

        //声明一个存在的队列用来取消息
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("Waiting for messages. 退出按CTRL + C");

        //以对象形式提供回调，队列会异步的传递消息过来
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
//        DeliverCallback deliverCallback =(consumerTag, delivery) -> {
//                String message = new String(delivery.getBody(), "UTF-8");
//        System.out.println("[x] Received'" + message + "'");
//        };
//        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag  -> {});
        channel.basicConsume(QUEUE_NAME, true, consumer);

        //不用关闭资源保持链接和通道alive随时接收消息
    }

}
