package p2p;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import connection.RmqConnection;

import java.util.concurrent.TimeoutException;

/**
 * @author mrguanxs@163.com
 * @date 2019/5/15 17:25
 */
public class Sender {

    //队列命名
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv)throws java.io.IOException, TimeoutException {
        //创建到服务器的链接
        Connection connection = RmqConnection.getProducerConnection();

        //创建通道
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "Hello World!";
        //发布消息到队列
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        channel.close();
        connection.close();
    }

}
