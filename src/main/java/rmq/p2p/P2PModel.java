package rmq.p2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rmq.work_queue.WorkQueueModel;

/**
 * @author Mr.Guan
 * @since 2019/11/14
 * 点对点模式
 */
public class P2PModel {
    private static Logger LOGGER = LoggerFactory.getLogger(P2PModel.class);


    /**
     * 发送点对点消息
     * @param queueName     队列名称
     * @param durable       是否持久化
     * @param message       消息内容
     */
    public static void p2PSend(String queueName, boolean durable, String message) {
        WorkQueueModel.workQueueSend(queueName,durable,message);
    }

    /**
     * 接收点对点消息
     * @param queueName     队列名称
     * @param durable       是否持久化
     */
    public static String p2PReceive(String queueName, boolean durable) {
        //最多接收没ack的消息个数(可做负载)
        int prefetchCount = 1;
        //自动确认消息
        boolean autoAck = false;
        return WorkQueueModel.workQueueReceive(queueName, durable, prefetchCount, autoAck);
    }

}
