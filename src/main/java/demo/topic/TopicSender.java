package demo.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import connection.RmqConnection;

/**
 * @author Mr.Guan
 * @since 2019/11/13
 */
public class TopicSender {

    protected static final String EXCHANGE_NAME = "topic_logs";
    protected static final String ROUTINGKEY = "dfd.ser.dfgdfg.111.dao";

    public static void main(String[] argv) throws Exception {

        Connection producerCon = RmqConnection.getProducerConnection();
        Channel channel = producerCon.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String message = "这是ROUTINGKEY：" + ROUTINGKEY;

        channel.basicPublish(EXCHANGE_NAME, ROUTINGKEY, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + ROUTINGKEY + "':'" + message + "'");
    }
}
