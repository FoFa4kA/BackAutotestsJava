package wordpressapi.steps;

import wordpressapi.pojos.postpojos.CreatePostRequest;
import wordpressapi.pojos.postpojos.CreatePostResponse;

import static wordpressapi.utils.Specs.REQ_SPEC;
import static wordpressapi.utils.Specs.getBasicAuth;
import static wordpressapi.utils.TestProps.FORCE;
import static wordpressapi.utils.TestProps.POSTS_ROUTE;
import static wordpressapi.utils.TestProps.REST_ROUTE;
import static wordpressapi.utils.TestProps.STAT_CODE_200;
import static wordpressapi.utils.TestProps.STAT_CODE_201;
import static wordpressapi.utils.TestProps.TRUE;

public class PostsSteps {

    public static CreatePostResponse createPost(String title, String content) {
        CreatePostRequest createPostRequest = new CreatePostRequest(title, content);

        return getBasicAuth()
                .spec(REQ_SPEC)
                .queryParam(REST_ROUTE, POSTS_ROUTE)
                .body(createPostRequest)
                .when().post()
                .then()
                .statusCode(STAT_CODE_201)
                .extract().as(CreatePostResponse.class);
    }

    public static void deletePost(int postId) {
        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, POSTS_ROUTE + postId,
                        FORCE, TRUE)
                .when().delete()
                .then()
                .statusCode(STAT_CODE_200);
    }
}
