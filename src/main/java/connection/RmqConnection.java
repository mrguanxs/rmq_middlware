package connection;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import config.RmqConfig;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author mrguanxs@163.com
 * @date 2019/5/16 17:20
 */
public class RmqConnection {
    private static Logger logger = LoggerFactory.getLogger(RmqConnection.class);
    private static Connection connection = null;

    private static Connection getConnection(){
        connectionValidate();
        //创建到服务器的链接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RmqConfig.RabbitmqHost);
        factory.setPort(RmqConfig.RabbitmqPort);
        factory.setUsername(RmqConfig.RabbitmqUsername);
        factory.setPassword(RmqConfig.RabbitmqPassword);
        factory.setVirtualHost(RmqConfig.RabbitmqVirtualHost);
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            logger.error("创建连接失败：" + e.getMessage(), e);
        } catch (TimeoutException e) {
            logger.error("创建链接超时" + e.getMessage(), e);
        }
        return connection;
    }

    /**
     * 连接参数验证
     */
    private static void connectionValidate() {
        if (StringUtils.isBlank(RmqConfig.RabbitmqHost)) {
            logger.error("RabbitmqHost is null");
            throw new RuntimeException("RabbitmqHost is null");
        } else if (RmqConfig.RabbitmqPort <= 0) {
            logger.error("RabbitmqPort error is null or <= 0 " + RmqConfig.RabbitmqPort);
            throw new RuntimeException("RabbitmqPort error is null or <= 0 " + RmqConfig.RabbitmqPort);
        } else if (StringUtils.isBlank(RmqConfig.RabbitmqUsername)) {
            logger.error("RabbitmqUsername is null");
            throw new RuntimeException("RabbitmqUsername is null");
        } else if (StringUtils.isBlank(RmqConfig.RabbitmqPassword)) {
            logger.error("RabbitmqPassword is null");
            throw new RuntimeException("RabbitmqPassword is null");
        }
    }

    public static Connection getProducerConnection(){
        if (connection == null){
            connection = getConnection();
        }
        return connection;
    }

    public static Connection getConsumerConnection(){
        if (connection == null){
            connection = getConnection();
        }
        return connection;
    }

}
