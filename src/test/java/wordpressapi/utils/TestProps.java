package wordpressapi.utils;

public abstract class TestProps {

    // routes
    public static final String POSTS_ROUTE = "/wp/v2/posts/";
    public static final String USERS_ROUTE = "/wp/v2/users/";
    public static final String USERS_ME_ROUTE = "/wp/v2/users/me";
    public static final String TAGS_ROUTE = "/wp/v2/tags/";

    // request params
    public static final String REST_ROUTE = "rest_route";
    public static final String FORCE = "force";
    public static final String REASSIGN = "reassign";

    // request params values
    public static final String TRUE = "true";

    // response codes
    public static final int STAT_CODE_200 = 200;
    public static final int STAT_CODE_201 = 201;
    public static final int STAT_CODE_404 = 404;
    public static final int STAT_CODE_500 = 500;
}
