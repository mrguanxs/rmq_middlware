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

    public static void main(String[] args) throws InterruptedException {
        String exchangeName = "testHeaders";
        Map<String, Object> headers = new Hashtable<>();
        headers.put("aaa", "a1a1a1");
        headers.put("bbb", "b2b2b2");

        String receive = HeadersModel.headersReceive(exchangeName, headers, true, MatchType.ALL);
        HeadersModel.headersSend(exchangeName, headers, "hahahah");
    }
}
