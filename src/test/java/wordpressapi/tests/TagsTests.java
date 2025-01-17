package wordpressapi.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wordpressapi.pojos.tagspojos.CreateTagRequest;
import wordpressapi.pojos.tagspojos.CreateTagResponse;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static wordpressapi.steps.TagsSteps.createTag;
import static wordpressapi.steps.TagsSteps.deleteTag;
import static wordpressapi.utils.GenerateString.getGeneratedString;
import static wordpressapi.utils.Specs.REQ_SPEC;
import static wordpressapi.utils.Specs.getBasicAuth;
import static wordpressapi.utils.TestProps.FORCE;
import static wordpressapi.utils.TestProps.REST_ROUTE;
import static wordpressapi.utils.TestProps.STAT_CODE_200;
import static wordpressapi.utils.TestProps.STAT_CODE_201;
import static wordpressapi.utils.TestProps.STAT_CODE_404;
import static wordpressapi.utils.TestProps.TAGS_ROUTE;
import static wordpressapi.utils.TestProps.TRUE;

@Epic("API Tests")
@Feature("Tags Tests")
public class TagsTests {
    String name = getGeneratedString(10);
    String description = getGeneratedString(25);
    CreateTagRequest createTagRequest = new CreateTagRequest(name, description);
    int newTagId;

    @Test
    @Story("Создание тега с именем и описанием")
    @Severity(SeverityLevel.MINOR)
    public void createTagTest() {
        CreateTagResponse createTagResponse = getBasicAuth()
                .spec(REQ_SPEC)
                .queryParam(REST_ROUTE, TAGS_ROUTE)
                .body(createTagRequest)
                .when().post()
                .then()
                .statusCode(STAT_CODE_201)
                .body("id", notNullValue(),
                        "count", equalTo(0),
                        "description", equalTo(description),
                        "link", equalTo("http://localhost:8000/?tag=" + name.toLowerCase()),
                        "name", equalTo(name),
                        "slug", equalTo(name.toLowerCase()),
                        "taxonomy", equalTo("post_tag"),
                        "meta", empty(),
                        "_links.self", hasSize(1),
                        "_links.self[0].href", startsWith("http://localhost:8000/index.php?rest_route=/wp/v2/tags/"),
                        "_links.self[0].targetHints.allow[0]", equalTo("GET"),
                        "_links.self[0].targetHints.allow[1]", equalTo("POST"),
                        "_links.self[0].targetHints.allow[2]", equalTo("PUT"),
                        "_links.self[0].targetHints.allow[3]", equalTo("PATCH"),
                        "_links.self[0].targetHints.allow[4]", equalTo("DELETE"),
                        "_links.collection", hasSize(1),
                        "_links.collection[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/tags"),
                        "_links.about", hasSize(1),
                        "_links.about[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/taxonomies/post_tag"),
                        "_links.'wp:post_type'", hasSize(1),
                        "_links.'wp:post_type'[0].href",
                        startsWith("http://localhost:8000/index.php?rest_route=%2Fwp%2Fv2%2Fposts&tags="),
                        "_links.curies", hasSize(1),
                        "_links.curies[0].name", equalTo("wp"),
                        "_links.curies[0].href", equalTo("https://api.w.org/{rel}"),
                        "_links.curies[0].templated", equalTo(true))
                .extract().as(CreateTagResponse.class);

        newTagId = createTagResponse.getId();

        getBasicAuth()
                .spec(REQ_SPEC)
                .param(REST_ROUTE, TAGS_ROUTE + newTagId)
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newTagId),
                        "description", equalTo(description),
                        "link", equalTo("http://localhost:8000/?tag=" + name.toLowerCase()),
                        "name", equalTo(name),
                        "slug", equalTo(name.toLowerCase()),
                        "_links.self[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/tags/" + newTagId)
                        , "_links.'wp:post_type'[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=%2Fwp%2Fv2%2Fposts&tags=" +
                                newTagId));
    }

    @Test
    @Story("Получение тега")
    @Severity(SeverityLevel.MINOR)
    public void retrieveTagTest() {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        CreateTagResponse createTagResponse = createTag(name,description);
        int newTagId = createTagResponse.getId();

        getBasicAuth()
                .spec(REQ_SPEC)
                .param(REST_ROUTE, TAGS_ROUTE + newTagId)
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newTagId),
                        "description", equalTo(description),
                        "link", equalTo("http://localhost:8000/?tag=" + name.toLowerCase()),
                        "name", equalTo(name),
                        "slug", equalTo(name.toLowerCase()),
                        "_links.self[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/tags/" + newTagId)
                        , "_links.'wp:post_type'[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=%2Fwp%2Fv2%2Fposts&tags=" +
                                newTagId)
                );
    }

    @Test
    @Story("Изменение описания поста")
    @Severity(SeverityLevel.MINOR)
    public void updateTagDescriptionTest() {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        CreateTagResponse createTagResponse = createTag(name,description);
        int newTagId = createTagResponse.getId();
        String updatedDescription = getGeneratedString(30);

        getBasicAuth()
                .spec(REQ_SPEC)
                .queryParams(REST_ROUTE, TAGS_ROUTE + newTagId,
                        "description", updatedDescription)
                .when().post()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newTagId),
                        "description", equalTo(updatedDescription));

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, TAGS_ROUTE + newTagId)
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newTagId),
                        "description", equalTo(updatedDescription));
    }

    @Test
    @Story("Удаление поста")
    @Severity(SeverityLevel.MINOR)
    public void deleteTagTest() {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        CreateTagResponse createTagResponse = createTag(name,description);
        int newTagId = createTagResponse.getId();

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, TAGS_ROUTE + newTagId,
                        FORCE, TRUE)
                .when().delete()
                .then()
                .statusCode(STAT_CODE_200)
                .body("deleted", equalTo(true),
                        "previous.id", equalTo(newTagId),
                        "previous.count", equalTo(0),
                        "previous.description", equalTo(description),
                        "previous.link", equalTo("http://localhost:8000/?tag=" + name.toLowerCase()),
                        "previous.name", equalTo(name),
                        "previous.slug", equalTo(name.toLowerCase()),
                        "previous.taxonomy", equalTo("post_tag"),
                        "previous.meta", empty());

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, TAGS_ROUTE + newTagId,
                        FORCE, TRUE)
                .when().delete()
                .then()
                .statusCode(STAT_CODE_404)
                .body("code", equalTo("rest_term_invalid"),
                        "message", equalTo("Элемент не существует."),
                        "data.status", equalTo(STAT_CODE_404));
    }

    @Test
    @Story("Получение списка постов с указанием страницы и кол-ом элементов на одной странице")
    @Severity(SeverityLevel.MINOR)
    public void getListTagsTest() {
        for (int i = 0; i < 5; i++) {
            String name = getGeneratedString(10);
            String description = getGeneratedString(25);
            CreateTagResponse createTagResponse = createTag(name,description);
        }

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, TAGS_ROUTE,
                        "page", "3",
                        "per_page", "2")
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("", hasSize(1));
    }

    @Test
    @Story("Попытка получения поста по некорректному id")
    @Severity(SeverityLevel.MINOR)
    public void attemptToGetTagWithInvalidId() {
        String invalidId = getGeneratedString(10);

        getBasicAuth()
                .spec(REQ_SPEC)
                .param(REST_ROUTE, TAGS_ROUTE + invalidId)
                .when().get()
                .then()
                .statusCode(STAT_CODE_404)
                .body("code", equalTo("rest_no_route"),
                        "message", equalTo("Подходящий маршрут для URL и метода запроса не найден."),
                        "data.status", equalTo(STAT_CODE_404));
    }

    @AfterMethod
    public void cleanUp() {
        List<CreateTagResponse> tags = getBasicAuth()
                .spec(REQ_SPEC)
                .param(REST_ROUTE, TAGS_ROUTE)
                .when().get()
                .then()
                .extract().jsonPath().getList("", CreateTagResponse.class);
        tags.forEach(tag -> deleteTag(tag.getId()));
    }
}