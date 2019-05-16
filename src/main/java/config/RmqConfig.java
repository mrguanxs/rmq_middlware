package config;

import utils.PropertyUtil;

import java.util.Properties;

/**
 * @author mrguanxs@163.com
 * @date 2019/5/16 13:57
 */
public class RmqConfig {

    private static final String rmqConfigPath = "/rmqConfig.properties";
    private static final Properties rmqProperties = PropertyUtil.getProperties(rmqConfigPath);

    public static final String RabbitmqHost = rmqProperties.getProperty("RabbitmqHost");
    public static final int RabbitmqPort = rmqProperties.getProperty("RabbitmqPort") != null ? Integer.parseInt(rmqProperties.getProperty("RabbitmqPort")) : 0;
    public static final String RabbitmqUsername = rmqProperties.getProperty("RabbitmqUsername");
    public static final String RabbitmqPassword = rmqProperties.getProperty("RabbitmqPassword");
    public static final String RabbitmqVirtualHost = rmqProperties.getProperty("RabbitmqVirtualHost");
}
