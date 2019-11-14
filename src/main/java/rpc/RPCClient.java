package rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import connection.RmqConnection;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Mr.Guan
 * @since 2019/11/14
 * 利用rabbbbitmq实现的远程过程调用的客户端代码
 */
public class RPCClient implements AutoCloseable{

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCClient() throws IOException {
        connection = RmqConnection.getProducerConnection();
        channel = connection.createChannel();
    }

    public static void main(String[] argv){
        try (RPCClient fibonacciRpc = new RPCClient()) {
            for (int i = 0; i < 32; i++) {
                String i_str = Integer.toString(i);
                System.out.println(" [x] Requesting fib(" + i_str + ")");
                String response = fibonacciRpc.call(i_str);
                System.out.println(" [.] Got '" + response + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String call(String message) throws IOException, InterruptedException {
        //生成唯一CorrelationId，调用完成后返回进行区分谁谁调用的
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
        //发送给服务端
        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));
        //没有返回前锁住此队列
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        //服务端回调
        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            //如果corrId一致说明是它调用的，可以返回，否则丢弃
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        channel.basicCancel(ctag);
        return result;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
