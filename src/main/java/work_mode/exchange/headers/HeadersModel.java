package work_mode.exchange.headers;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import connection.RmqConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author Mr.Guan
 * @since 2019/11/14
 * 与topic差不多，只不过routingKey不是字符串，而是键值对，用的较少
 */
public class HeadersModel {
    private static Logger LOGGER = LoggerFactory.getLogger(HeadersModel.class);


    /**
     * 发送headers消息
     * @param exchangeName  交换机名称
     * @param headers       匹配规则，作用类似routingKey
     * @param message       消息内容
     */
    public static void headersSend(String exchangeName, Map<String, Object> headers, String message) {
        LOGGER.info("准备发送headers消息，exchangeName:{},headers:{},message:{}",exchangeName, headers.toString(), message);
        Connection proCon = RmqConnection.getProducerConnection();
        Channel channel = null;
        try{
            channel = proCon.createChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.HEADERS, false,false, headers);

            AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
            properties.headers(headers);
            channel.basicPublish(exchangeName, "", properties.build(), message.getBytes("UTF-8"));
        }catch (IOException e){
            LOGGER.error("headers消息发送失败，exchangeName:{},message:{}",exchangeName, message);
        }finally {
            try {
                channel.close();
                proCon.close();
            } catch (IOException e) {
                LOGGER.error("headers连接关闭失败",e.getMessage());
            } catch (TimeoutException e) {
                LOGGER.error("headers连接关闭失败，超时",e.getMessage());
            }
        }
        LOGGER.info("headers消息发送完成，exchangeName:{},headers:{},message:{}",exchangeName, headers.toString(), message);
    }

    /**
     *
     * headers接收
     * @param exchangeName  交换机名称
     * @param headers       匹配规则，作用类似routingKey
     * @param autoAck       自动确认消息
     */
    public static String headersReceive(String exchangeName, Map<String, Object> headers, boolean autoAck, MatchType matchType) {
        Map<String,Object> matchHeaders = new Hashtable<>();
        matchHeaders.put("x-match", matchType.getType());
        matchHeaders.putAll(headers);
        LOGGER.info("开始接收headers消息，exchangeName:{},matchHeaders:{}",exchangeName, matchHeaders.toString());
        StringBuilder result = new StringBuilder();

        try{
            Connection consumerCon = RmqConnection.getConsumerConnection();
            Channel channel = consumerCon.createChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.HEADERS);

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, "", matchHeaders);

            //以对象形式提供回调，队列会异步的传递消息过来
            DeliverCallback deliverCallback =(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                result.append(message);
                if(!autoAck) {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag ->{});

        }catch (IOException e){
            LOGGER.error("headers消息接收失败,exchangeName:{},matchHeaders:{}",exchangeName, matchHeaders.toString(), result.toString());
        }
        return result.toString();
    }
}
