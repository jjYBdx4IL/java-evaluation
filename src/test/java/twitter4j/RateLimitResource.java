package twitter4j;

public enum RateLimitResource {

    SEARCH_TWEETS("/search/tweets"),
    STATUSES_HOME_TIMELINE("/statuses/home_timeline"),
    STATUSES_LOOKUP("/statuses/lookup"),
    STATUSES_USER_TIMELINE("/statuses/user_timeline");

    public final String value;

    RateLimitResource(String value) {
        this.value = value;
    }
}
