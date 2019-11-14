package work_mode.headers;

/**
 * @author Mr.Guan
 * @since 2019/11/14
 */
public enum MatchType {

    ALl("all"), ANY("any");

    private final String type;

    private MatchType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
