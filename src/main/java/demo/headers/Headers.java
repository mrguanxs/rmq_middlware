package demo.headers;

import work_mode.exchange.headers.HeadersModel;
import work_mode.exchange.headers.MatchType;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author Mr.Guan
 * @since 2019/11/14
 */
public class Headers {

    public static void main(String[] args) {
        String exchangeName = "testHeaders";
        Map<String,Object> headers = new Hashtable<>();
        headers.put("aaa", "a1a1a1");
        headers.put("bbb", "b2b2b2");
        HeadersModel.headersSend(exchangeName, headers, "hahahah");
        String receive = HeadersModel.headersReceive(exchangeName, headers, true, MatchType.ALL);
        System.out.println("receive:" + receive);
    }
}
