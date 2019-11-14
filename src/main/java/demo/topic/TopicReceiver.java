package demo.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import connection.RmqConnection;

/**
 * @author Mr.Guan
 * @since 2019/11/13
 */
public class TopicReceiver {


    public static void main(String[] argv) throws Exception {
        rec("*.ser.*");
    }

    private static void rec(String bindingKey) throws Exception{
        Connection connection = RmqConnection.getConsumerConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(TopicSender.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();


        channel.queueBind(queueName, TopicSender.EXCHANGE_NAME, bindingKey);

        System.out.println(" [*] Waiting for messages. bindingKey:" + bindingKey);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
