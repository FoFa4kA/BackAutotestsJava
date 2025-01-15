package wordpressapi.steps;

import wordpressapi.pojos.tagspojos.CreateTagRequest;
import wordpressapi.pojos.tagspojos.CreateTagResponse;

import static wordpressapi.utils.Specs.REQ_SPEC;
import static wordpressapi.utils.Specs.getBasicAuth;
import static wordpressapi.utils.TestProps.FORCE;
import static wordpressapi.utils.TestProps.REST_ROUTE;
import static wordpressapi.utils.TestProps.STAT_CODE_200;
import static wordpressapi.utils.TestProps.STAT_CODE_201;
import static wordpressapi.utils.TestProps.TAGS_ROUTE;
import static wordpressapi.utils.TestProps.TRUE;

public class TagsSteps {

    public static CreateTagResponse createTag(String name, String description) {
        CreateTagRequest createTagRequest = new CreateTagRequest(name, description);

        return getBasicAuth()
                .spec(REQ_SPEC)
                .queryParam(REST_ROUTE, TAGS_ROUTE)
                .body(createTagRequest)
                .when().post()
                .then()
                .statusCode(STAT_CODE_201)
                .extract().as(CreateTagResponse.class);
    }

    public static void deleteTag(int tagId) {
        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, TAGS_ROUTE + tagId,
                        FORCE, TRUE)
                .when().delete()
                .then()
                .statusCode(STAT_CODE_200);
    }
}
