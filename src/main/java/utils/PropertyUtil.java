package utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author mrguanxs@163.com
 * @date 2019/5/16 14:11
 */
public class PropertyUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
    
    public static Properties getProperties(String classPath){
        Properties properties = null;
        try {
            properties=new Properties();
            InputStream resourceAsStream = PropertyUtil.class.getClass().getResourceAsStream(classPath);
            properties.load(resourceAsStream);
            resourceAsStream.close();
        } catch (IOException e) {
            logger.error("获取" + classPath + "文件信息失败：" + e.getMessage());
        }
        return properties;
    }
}
