package wordpressapi.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wordpressapi.pojos.postpojos.CreatePostRequest;
import wordpressapi.pojos.postpojos.CreatePostResponse;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static wordpressapi.steps.PostsSteps.createPost;
import static wordpressapi.steps.PostsSteps.deletePost;
import static wordpressapi.utils.DateDeserializer.DATE_TIME_PATTERN;
import static wordpressapi.utils.GenerateString.getGeneratedString;
import static wordpressapi.utils.Specs.REQ_SPEC;
import static wordpressapi.utils.Specs.getBasicAuth;
import static wordpressapi.utils.TestProps.FORCE;
import static wordpressapi.utils.TestProps.POSTS_ROUTE;
import static wordpressapi.utils.TestProps.REST_ROUTE;
import static wordpressapi.utils.TestProps.STAT_CODE_200;
import static wordpressapi.utils.TestProps.STAT_CODE_201;
import static wordpressapi.utils.TestProps.STAT_CODE_404;
import static wordpressapi.utils.TestProps.TRUE;

@Epic("API Tests")
@Feature("Posts Tests")
public class PostsTests {

    @Test
    @Story("Получение поста")
    @Severity(SeverityLevel.NORMAL)
    public void retrievePostTest() {
        String newPostTitle = getGeneratedString(10);
        String newPostContent = getGeneratedString(20);
        CreatePostResponse createPostResponse = createPost(newPostTitle, newPostContent);
        int newPostId = createPostResponse.getId();

        getBasicAuth()
                .spec(REQ_SPEC)
                .param(REST_ROUTE, POSTS_ROUTE + newPostId)
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newPostId),
                        "date", equalTo(createPostResponse.getDate().format(DateTimeFormatter
                                .ofPattern(DATE_TIME_PATTERN))),
                        "date_gmt", equalTo(createPostResponse.getDateGmt().format(DateTimeFormatter
                                .ofPattern(DATE_TIME_PATTERN))),
                        "guid.rendered", equalTo("http://localhost:8000/?p=" + newPostId),
                        "modified", equalTo(createPostResponse.getDate().format(DateTimeFormatter
                                .ofPattern(DATE_TIME_PATTERN))),
                        "modified_gmt", equalTo(createPostResponse.getDateGmt().format(DateTimeFormatter
                                .ofPattern(DATE_TIME_PATTERN))),
                        "slug", emptyString(),
                        "status", equalTo("draft"),
                        "type", equalTo("post"),
                        "link", equalTo("http://localhost:8000/?p=" + newPostId),
                        "title.rendered", equalTo(createPostResponse.getTitle().getRendered()),
                        "content.rendered", equalTo(createPostResponse.getContent().getRendered()),
                        "content.protected", equalTo(false),
                        "excerpt.rendered", equalTo(createPostResponse.getContent().getRendered()),
                        "excerpt.protected", equalTo(false),
                        "author", equalTo(1),
                        "featured_media", equalTo(0),
                        "comment_status", equalTo("open"),
                        "ping_status", equalTo("open"),
                        "sticky", equalTo(false),
                        "template", emptyString(),
                        "format", equalTo("standard"),
                        "meta.footnotes", emptyString(),
                        "categories[0]", equalTo(1),
                        "tags", empty(),
                        "class_list[0]", equalTo("post-" + newPostId),
                        "class_list[1]", equalTo("post"),
                        "class_list[2]", equalTo("type-post"),
                        "class_list[3]", equalTo("status-draft"),
                        "class_list[4]", equalTo("format-standard"),
                        "class_list[5]", equalTo("hentry"),
                        "class_list[6]", equalTo("category-1"),
                        "_links.self[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/posts/" + newPostId),
                        "_links.self[0].targetHints.allow[0]", equalTo("GET"),
                        "_links.self[0].targetHints.allow[1]", equalTo("POST"),
                        "_links.self[0].targetHints.allow[2]", equalTo("PUT"),
                        "_links.self[0].targetHints.allow[3]", equalTo("PATCH"),
                        "_links.self[0].targetHints.allow[4]", equalTo("DELETE"),
                        "_links.collection[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/posts"),
                        "_links.about[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/types/post"),
                        "_links.author[0].embeddable", equalTo(true),
                        "_links.author[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/users/1"),
                        "_links.replies[0].embeddable", equalTo(true),
                        "_links.replies[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=%2Fwp%2Fv2%2Fcomments&post=" +
                                newPostId),
                        "_links.version-history[0].count", equalTo(0),
                        "_links.version-history[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/posts/" + newPostId +
                                "/revisions"),
//                        "_links.wp:attachment[0].href",
//                        equalTo("http://localhost:8000/index.php?rest_route=%2Fwp%2Fv2%2Fmedia&parent=" +
//                                newPostId),
//                        "_links.wp:term[0].taxonomy", equalTo("category"),
//                        "_links.wp:term[0].embeddable", equalTo(true),
//                        "_links.wp:term[0].href",
//                        equalTo("http://localhost:8000/index.php?rest_route=%2Fwp%2Fv2%2Fcategories&post=" +
//                                newPostId),
//                        "_links.wp:term[1].taxonomy", equalTo("post_tag"),
//                        "_links.wp:term[1].embeddable", equalTo(true),
//                        "_links.wp:term[1].href",
//                        equalTo("http://localhost:8000/index.php?rest_route=%2Fwp%2Fv2%2Ftags&post=" +
//                                newPostId),
                        "_links.curies[0].name", equalTo("wp"),
                        "_links.curies[0].href", equalTo("https://api.w.org/{rel}"),
                        "_links.curies[0].templated", equalTo(true));
    }
    
    @Test
    @Story("Создание поста с заголовком и содержанием")
    @Severity(SeverityLevel.NORMAL)
    public void createPostTest() {
        String newPostTitle = getGeneratedString(10);
        String newPostContent = getGeneratedString(20);
        CreatePostRequest createPostRequest = new CreatePostRequest(newPostTitle, newPostContent);
        String currentDate = LocalDate.now().toString();
        String currentDateGmt = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int newPostId;

        CreatePostResponse createPostResponse = getBasicAuth()
                .spec(REQ_SPEC)
                .queryParam(REST_ROUTE, POSTS_ROUTE)
                .body(createPostRequest)
                .when().post()
                .then()
                .statusCode(STAT_CODE_201)
                .body("date", startsWith(currentDate),
                        "date_gmt", startsWith(currentDateGmt),
                        "title.raw", equalTo(newPostTitle),
                        "title.rendered", equalTo(newPostTitle),
                        "content.raw", equalTo(newPostContent),
                        "content.rendered", containsString(newPostContent),
                        "excerpt.rendered", containsString(newPostContent))
                .extract().as(CreatePostResponse.class);

        newPostId = createPostResponse.getId();

        getBasicAuth()
                .spec(REQ_SPEC)
                .param(REST_ROUTE, POSTS_ROUTE + newPostId)
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newPostId),
                        "title.rendered", equalTo(newPostTitle),
                        "content.rendered", containsString(createPostRequest.getContent()),
                        "excerpt.rendered", containsString(createPostRequest.getContent()));
    }

    @Test
    @Story("Изменение заголовка поста")
    @Severity(SeverityLevel.NORMAL)
    public void updatePostTitleTest() {
        String newPostTitle = getGeneratedString(10);
        String newPostContent = getGeneratedString(20);
        CreatePostResponse createPostResponse = createPost(newPostTitle, newPostContent);
        int newPostId = createPostResponse.getId();
        String updatedTitle = getGeneratedString(15);

        getBasicAuth()
                .spec(REQ_SPEC)
                .queryParams(REST_ROUTE, POSTS_ROUTE + newPostId,
                            "title", updatedTitle)
                .when().post()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newPostId),
                        "title.raw", equalTo(updatedTitle),
                        "title.rendered", equalTo(updatedTitle));

        getBasicAuth()
                .spec(REQ_SPEC)
                .param(REST_ROUTE, POSTS_ROUTE + newPostId)
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newPostId),
                        "title.rendered", equalTo(updatedTitle));
    }

    @Test
    @Story("Получение списка постов с поиском по тексту")
    @Severity(SeverityLevel.NORMAL)
    public void getListPostsTest() {
        String searchTitle = "";
        String newPostTitle;
        String newPostContent;
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                newPostTitle = getGeneratedString(10);
                searchTitle = newPostTitle;
                newPostContent = getGeneratedString(20);
            } else {
                newPostTitle = getGeneratedString(15);
                newPostContent = getGeneratedString(20);
            }
            createPost(newPostTitle, newPostContent);
        }

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, POSTS_ROUTE,
                        "status", "draft",
                        "search", searchTitle.toLowerCase())
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("", hasSize(1),
                        "[0].title.rendered", equalTo(searchTitle));
    }

    @Test
    @Story("Удаление поста")
    @Severity(SeverityLevel.NORMAL)
    public void deletePostTest() {
        String newPostTitle = getGeneratedString(10);
        String newPostContent = getGeneratedString(20);
        CreatePostResponse createPostResponse = createPost(newPostTitle, newPostContent);
        int newPostId = createPostResponse.getId();

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, POSTS_ROUTE + newPostId,
                        FORCE, TRUE)
                .when().delete()
                .then()
                .statusCode(STAT_CODE_200)
                .body("deleted", equalTo(true),
                        "previous.id", equalTo(newPostId),
                        "previous.title.raw", equalTo(newPostTitle),
                        "previous.content.raw", equalTo(newPostContent));

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, POSTS_ROUTE + newPostId,
                        FORCE, TRUE)
                .when().delete()
                .then()
                .statusCode(STAT_CODE_404)
                .body("code", equalTo("rest_post_invalid_id"),
                        "message", equalTo("Неверный ID записи."),
                        "data.status", equalTo(STAT_CODE_404));
    }

    @Test
    @Story("Попытка удаления поста с некорректным id")
    @Severity(SeverityLevel.NORMAL)
    public void attemptToDeletePostWithInvalidId() {
        String invalidId = getGeneratedString(5);

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, POSTS_ROUTE + invalidId,
                        FORCE, TRUE)
                .when().delete()
                .then()
                .statusCode(STAT_CODE_404)
                .body("code", equalTo("rest_no_route"),
                        "message", equalTo("Подходящий маршрут для URL и метода запроса не найден."),
                        "data.status", equalTo(STAT_CODE_404));
    }

    @AfterMethod
    public void cleanUp() {
        List<CreatePostResponse> posts = getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, POSTS_ROUTE,
                "status", "draft")
                .when().get()
                .then()
                .extract().jsonPath().getList("", CreatePostResponse.class);
        posts.forEach(post -> deletePost(post.getId()));
    }
}
