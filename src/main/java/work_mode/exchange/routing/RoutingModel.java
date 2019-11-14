package work_mode.exchange.routing;

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
 * 路由模式，根据routingkey确定接收哪些消息(有选择的接收消息)
 */
public class RoutingModel {
    private static Logger LOGGER = LoggerFactory.getLogger(RoutingModel.class);


    /**
     * 发送订阅消息
     * @param exchangeName  交换机名称
     * @param routingKey    路由键
     * @param message       消息内容
     */
    public static void routingSend(String exchangeName, String routingKey, String message) {
        LOGGER.info("准备发送订阅消息，exchangeName:{},routingKey:{},message:{}",exchangeName, routingKey, message);
        Connection proCon = RmqConnection.getProducerConnection();
        Channel channel = null;
        try{
            channel = proCon.createChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
        }catch (IOException e){
            LOGGER.error("订阅消息发送失败，exchangeName:{},message:{}",exchangeName, message);
        }finally {
            try {
                channel.close();
                proCon.close();
            } catch (IOException e) {
                LOGGER.error("订阅连接关闭失败",e.getMessage());
            } catch (TimeoutException e) {
                LOGGER.error("订阅连接关闭失败，超时",e.getMessage());
            }
        }
        LOGGER.info("订阅消息发送完成，exchangeName:{},routingKey:{},message:{}",exchangeName, routingKey, message);
    }

    /**
     *
     * 接收广播
     * @param exchangeName  交换机名称
     * @param routingKey    路由键
     * @param autoAck       自动确认消息
     */
    public static String routingReceive(String exchangeName, String routingKey, boolean autoAck) {
        LOGGER.info("开始接收订阅消息，exchangeName:{},routingKey:{}",exchangeName, routingKey);
        StringBuilder result = new StringBuilder();

        try{
            Connection consumerCon = RmqConnection.getConsumerConnection();
            Channel channel = consumerCon.createChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

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
            LOGGER.error("订阅消息接收失败,exchangeName:{},routingKey:{}",exchangeName, routingKey, result.toString());
        }
        return result.toString();
    }
}
