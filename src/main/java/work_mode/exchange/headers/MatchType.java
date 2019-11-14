package work_mode.exchange.headers;

/**
 * @author Mr.Guan
 * @since 2019/11/14
 */
public enum MatchType {

    ALL("all"), ANY("any");

    private final String type;

    private MatchType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
