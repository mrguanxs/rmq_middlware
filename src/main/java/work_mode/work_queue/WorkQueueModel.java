package work_mode.work_queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import connection.RmqConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author Mr.Guan
 * @since 2019/11/14
 * 延迟队列，解决高并发
 */
public class WorkQueueModel {
    private static Logger LOGGER = LoggerFactory.getLogger(WorkQueueModel.class);


    /**
     * 发送消息到工作队列
     * @param queueName     队列名称
     * @param durable       是否持久化
     * @param message       消息内容
     */
    public static void workQueueSend(String queueName, boolean durable, String message) {
        LOGGER.info("准备发送点对点消息，queueName:{0},message:{1}",queueName, message);
        Connection proCon = RmqConnection.getProducerConnection();
        Channel channel = null;
        try{
            channel = proCon.createChannel();
            queueDeclare(channel, queueName, durable);
            channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
        }catch (IOException e){
            LOGGER.error("点对点消息发送失败，queueName:{0},message:{1}",queueName, message);
        }finally {
            try {
                channel.close();
                proCon.close();
            } catch (IOException e) {
                LOGGER.error("连接关闭失败",e.getMessage());
            } catch (TimeoutException e) {
                LOGGER.error("连接关闭失败，超时",e.getMessage());
            }
        }
        LOGGER.info("点对点消息发送完成，queueName:{0},message:{1}",queueName, message);
    }


    /**
     *
     * 接收消息从工作队列
     * @param queueName     队列名称
     * @param durable       是否持久化
     * @param prefetchCount 最多接收没ack的消息个数(可做负载)
     * @param autoAck       自动确认消息
     */
    public static String workQueueReceive(String queueName, boolean durable, int prefetchCount, boolean autoAck) {
        LOGGER.info("准备接收点对点消息，queueName:{0}",queueName);
        StringBuilder result = new StringBuilder();

        try{
            Connection consumerCon = RmqConnection.getConsumerConnection();
            Channel channel = consumerCon.createChannel();
            channel.basicQos(prefetchCount);
            queueDeclare(channel, queueName, durable);

            //以对象形式提供回调，队列会异步的传递消息过来
            DeliverCallback deliverCallback =(consumerTag, delivery) -> {
                result.append(new String(delivery.getBody(), "UTF-8"));
                if(!autoAck) {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag ->{});

        }catch (IOException e){
            LOGGER.error("点对点消息接收失败，queueName:{0},message:{1}",queueName, result.toString());
        }
        return result.toString();
    }


    private static void queueDeclare(Channel channel, String queueName, boolean durable) throws IOException {
        //持久化
        //boolean durable = false;
        //排它,其它连接不能消费
        boolean exclusive = false;
        //自动删除
        boolean autoDelete = false;
        //其它参数
        Map<String, Object> arguments = null;
        channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
    }
}
