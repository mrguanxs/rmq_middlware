package work_mode.exchange.pub_sub;

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
 * 广播,类似于微信群@所有人的操作，所有绑定此exchange的队列的消费者都将收到广播，具有瞬时性；
 */
public class PubSubModel {
    private static Logger LOGGER = LoggerFactory.getLogger(PubSubModel.class);


    /**
     * 发送消息到工作队列
     * @param exchangeName     交换机名称
     * @param message       消息内容
     */
    public static void fanoutSend(String exchangeName, String message) {
        LOGGER.info("准备发送广播，exchangeName:{},message:{}",exchangeName, message);
        Connection proCon = RmqConnection.getProducerConnection();
        Channel channel = null;
        try{
            channel = proCon.createChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);
            channel.basicPublish(exchangeName, "", null, message.getBytes("UTF-8"));
        }catch (IOException e){
            LOGGER.error("广播发送失败，exchangeName:{},message:{}",exchangeName, message);
        }finally {
            try {
                channel.close();
                proCon.close();
            } catch (TimeoutException e) {
                LOGGER.error("连接关闭失败，超时",e.getMessage());
            } catch (IOException e) {
                LOGGER.error("连接关闭失败",e.getMessage());
            }
        }
        LOGGER.info("广播发送完成，exchangeName:{},message:{}",exchangeName, message);
    }

    /**
     *
     * 接收广播
     * @param exchangeName  交换机名称
     * @param autoAck       自动确认消息
     */
    public static String fanoutReceive(String exchangeName, boolean autoAck) {
        LOGGER.info("开始接收广播，exchangeName:{}",exchangeName);
        StringBuilder result = new StringBuilder();

        try{
            Connection consumerCon = RmqConnection.getConsumerConnection();
            Channel channel = consumerCon.createChannel();
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, "");

            //以对象形式提供回调，队列会异步的传递消息过来
            DeliverCallback deliverCallback =(consumerTag, delivery) -> {
                result.append(new String(delivery.getBody(), "UTF-8"));
                if(!autoAck) {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag ->{});

        }catch (IOException e){
            LOGGER.error("广播消息接收失败，exchangeName:{},message:{}",exchangeName, result.toString());
        }
        return result.toString();
    }


}
