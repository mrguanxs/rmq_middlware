package work_mode.exchange.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import connection.RmqConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Mr.Guan
 * @since 2019/11/14
 * 主题模式，跟路由模式差不多，只不过routingKey以.分割成好几段，消费者可以只订阅routingKey的一部分
 */
public class TopicModel {
    private static Logger LOGGER = LoggerFactory.getLogger(TopicModel.class);


    /**
     * 发送订阅消息
     * @param exchangeName  交换机名称
     * @param routingKey    路由键（格式 *.*.*,一个.代表一段，*任意匹配一段，#任意匹配多段）
     * @param message       消息内容
     */
    public static void topicSend(String exchangeName, String routingKey, String message) {
        LOGGER.info("准备发送主题消息，exchangeName:{},routingKey:{},message:{}",exchangeName, routingKey, message);
        Connection proCon = RmqConnection.getProducerConnection();
        Channel channel = null;
        try{
            channel = proCon.createChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
        }catch (IOException e){
            LOGGER.error("主题消息发送失败，exchangeName:{},message:{}",exchangeName, message);
        }finally {
            try {
                channel.close();
                proCon.close();
            } catch (IOException e) {
                LOGGER.error("主题连接关闭失败",e.getMessage());
            } catch (TimeoutException e) {
                LOGGER.error("主题连接关闭失败，超时",e.getMessage());
            }
        }
        LOGGER.info("主题消息发送完成，exchangeName:{},routingKey:{},message:{}",exchangeName, routingKey, message);
    }

    /**
     *
     * 主题接收
     * @param exchangeName  交换机名称
     * @param routingKey    路由键（格式 *.*.*,一个.代表一段，*任意匹配一段，#任意匹配多段）
     * @param autoAck       自动确认消息
     */
    public static String topicReceive(String exchangeName, String routingKey, boolean autoAck) {
        LOGGER.info("开始接收主题消息，exchangeName:{},routingKey:{}",exchangeName, routingKey);
        StringBuilder result = new StringBuilder();

        try{
            Connection consumerCon = RmqConnection.getConsumerConnection();
            Channel channel = consumerCon.createChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, routingKey);

            //以对象形式提供回调，队列会异步的传递消息过来
            DeliverCallback deliverCallback =(consumerTag, delivery) -> {
                result.append(new String(delivery.getBody(), "UTF-8"));
                if(!autoAck) {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag ->{});

        }catch (IOException e){
            LOGGER.error("主题消息接收失败,exchangeName:{},routingKey:{}",exchangeName, routingKey, result.toString());
        }
        return result.toString();
    }
}
