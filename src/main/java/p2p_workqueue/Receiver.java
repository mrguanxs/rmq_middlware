package p2p_workqueue;

import com.rabbitmq.client.*;
import connection.RmqConnection;

/**
 * @author mrguanxs@163.com
 * @date 2019/5/15 17:30
 */
public class Receiver {

    public static void main(String[] argv) throws Exception {
        //获取链接
        Connection connection = RmqConnection.getConsumerConnection();
        //获取通道
        Channel channel = connection.createChannel();

        //多劳多得，不设置此值则消费者轮流接收消息，公平竞争，注意：一个消息只能被一个消费者接收
        /**
         * 设置客户端最多接收没ack的消息个数
         * prefetchCount：会告诉RabbitMQ不要同时给一个消费者推送多于N个消息，即一旦有N个消息还没有ack，则该consumer将block掉，直到有消息ack
         */
        channel.basicQos(10);

        //声明一个存在的队列用来取消息
        /**
         * queue String类型,表示声明的queue对列的名字
         * durable Boolean类型,表示是否持久化
         * exclusive Boolean类型:当前声明的queue是否排他;true当前连接创建的任何channle都可以连接这个queue,false,新的channel不可使用
         * autoDelete Boolean类型:在最后连接使用完成后,是否删除队列
         * arguments Map类型,其他声明参数
         */
        channel.queueDeclare(Sender.QUEUE_NAME, false, false, false, null);
        System.out.println("Waiting for messages. 退出按CTRL + C");

        //以对象形式提供回调，队列会异步的传递消息过来
        DeliverCallback deliverCallback =(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("consumerTag:" + consumerTag);
            //手动消息确认
            /**
             * deliveryTag:该消息的index
             * multiple：是否批量.true:将一次性ack所有小于deliveryTag的消息
             */
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        System.out.println("[x] Received'" + message + "'");
        };
        //回调
        /**
         * queue 队列名
         * autoAck 是否自动确认消息,true自动确认,false不自动要手动调用,建议设置为false
         * deliverCallback 要回调的对象
         * consumerTag 消费者标签，用来区分多个消费者
         */
        channel.basicConsume(Sender.QUEUE_NAME, false, deliverCallback, consumerTag ->{});

        //同上方法一样的，只不过上述方法写法更简单，最终执行的还是DefaultConsumer的handleDelivery方法
//        Consumer consumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
//                    throws IOException {
//                String message = new String(body, "UTF-8");
//                System.out.println(" [x] Received '" + message + "'");
//            }
//        };
//        channel.basicConsume(Sender.QUEUE_NAME, true, consumer);

        //不用关闭资源保持链接和通道alive随时接收消息
    }

}
