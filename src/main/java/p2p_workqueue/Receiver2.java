package p2p_workqueue;


/**
 * @author mrguanxs@163.com
 * @date 2019/5/15 17:30
 */
public class Receiver2 {

    /**
     * 测试多个消费者
     */
    public static void main(String[] argv) throws Exception {
//        Connection connection = RmqConnection.getConsumerConnection();
//        Channel channel = connection.createChannel();
//        channel.queueDeclare(Sender.QUEUE_NAME, false, false, false, null);
//        System.out.println("Waiting for messages. 退出按CTRL + C");
//        DeliverCallback deliverCallback =(consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), "UTF-8");
//            System.out.println("consumerTag:" + consumerTag);
//            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//            System.out.println("[x] Received'" + message + "'");
//        };
//        channel.basicConsume(Sender.QUEUE_NAME, false, deliverCallback, consumerTag ->{});

    }

}
