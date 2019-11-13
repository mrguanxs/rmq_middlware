package config;

/**
 * @author Mr.Guan
 * @since 2019/11/13
 * 交换机的交换类型
 */
public interface ExchangeType {

    /**
     * 订阅
     */
    public static final String DIRECT = "direct";

    public static final String TOPIC = "topic";

    public static final String HEADERS = "headers";
    /**
     * 广播
     */
    public static final String FANOUT = "fanout";
}
