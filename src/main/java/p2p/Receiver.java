package p2p;

import com.rabbitmq.client.*;
import connection.RmqConnection;

import java.io.IOException;

/**
 * @author mrguanxs@163.com
 * @date 2019/5/15 17:30
 */
public class Receiver {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        //打开链接和通道
        Connection connection = RmqConnection.getConsumerConnection();
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
