package demo.p2p_workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import connection.RmqConnection;

import java.util.concurrent.TimeoutException;

/**
 * @author mrguanxs@163.com
 * @date 2019/5/15 17:25
 */
public class Sender {

    //队列命名
    protected final static String QUEUE_NAME = "hello";

    public static void main(String[] argv)throws java.io.IOException, TimeoutException {
        //创建到服务器的链接
        Connection connection = RmqConnection.getProducerConnection();

        //创建通道
        Channel channel = connection.createChannel();

        //声明队列
        /**
         * QUEUE_NAME 队列名
         * b 是否持久化
         * b1 是否排外  即只允许该channel访问该队列   一般等于true的话用于一个队列只能有一个消费者来消费的场景
         * b2 是否自动删除  消费完删除
         * map 其他属性
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = "Hello World!";

        //发布消息到队列
        /**
         * s 交换机
         * QUEUE_NAME 队列名
         * basicProperties 其他属性  路由
         * 消息body
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        //最后关闭通道和连接
        channel.close();
        connection.close();
    }

}
