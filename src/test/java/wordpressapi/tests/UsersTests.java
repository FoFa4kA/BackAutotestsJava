package wordpressapi.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wordpressapi.pojos.userpojos.CreateUserRequest;
import wordpressapi.pojos.userpojos.CreateUserResponse;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static wordpressapi.steps.UsersSteps.createUser;
import static wordpressapi.steps.UsersSteps.deleteUser;
import static wordpressapi.utils.GenerateString.getGeneratedString;
import static wordpressapi.utils.Specs.REQ_SPEC;
import static wordpressapi.utils.Specs.getBasicAuth;
import static wordpressapi.utils.TestProps.FORCE;
import static wordpressapi.utils.TestProps.REASSIGN;
import static wordpressapi.utils.TestProps.REST_ROUTE;
import static wordpressapi.utils.TestProps.STAT_CODE_200;
import static wordpressapi.utils.TestProps.STAT_CODE_201;
import static wordpressapi.utils.TestProps.STAT_CODE_404;
import static wordpressapi.utils.TestProps.STAT_CODE_500;
import static wordpressapi.utils.TestProps.TRUE;
import static wordpressapi.utils.TestProps.USERS_ME_ROUTE;
import static wordpressapi.utils.TestProps.USERS_ROUTE;

@Epic("API Tests")
@Feature("Users Tests")
public class UsersTests {

    @Test
    @Story("Создание пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserTest() {
        String username = getGeneratedString(10);
        String email = getGeneratedString(10) + "@gmail.com";
        String password = getGeneratedString(10);
        CreateUserRequest createUserRequest = new CreateUserRequest(username, password, email);
        String currentDateGmt = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int newUserId;

        CreateUserResponse createUserResponse = getBasicAuth()
                .spec(REQ_SPEC)
                .queryParam(REST_ROUTE, USERS_ROUTE)
                .body(createUserRequest)
                .when().post()
                .then()
                .statusCode(STAT_CODE_201)
                .body("id", notNullValue(),
                        "username", equalTo(username),
                        "name", equalTo(username),
                        "first_name", emptyString(),
                        "last_name", emptyString(),
                        "email", equalTo(email),
                        "url", emptyString(),
                        "description", emptyString(),
                        "link", startsWith("http://localhost:8000/?author="),
                        "locale", equalTo("ru_RU"),
                        "nickname", equalTo(username),
                        "slug", equalTo(username.toLowerCase()),
                        "roles[0]", equalTo("subscriber"),
                        "registered_date", startsWith(currentDateGmt),
                        "capabilities.read", equalTo(true),
                        "capabilities.level_0", equalTo(true),
                        "capabilities.subscriber", equalTo(true),
                        "extra_capabilities.subscriber", equalTo(true),
                        "avatar_urls.24", startsWith("https://secure.gravatar.com/avatar/"),
                        "avatar_urls.48", startsWith("https://secure.gravatar.com/avatar/"),
                        "avatar_urls.96", startsWith("https://secure.gravatar.com/avatar/"),
                        "meta.persisted_preferences", empty(),
                        "_links.self[0].href", startsWith("http://localhost:8000/index.php?rest_route=/wp/v2/users/"),
                        "_links.self[0].targetHints.allow[0]", equalTo("GET"),
                        "_links.self[0].targetHints.allow[1]", equalTo("POST"),
                        "_links.self[0].targetHints.allow[2]", equalTo("PUT"),
                        "_links.self[0].targetHints.allow[3]", equalTo("PATCH"),
                        "_links.self[0].targetHints.allow[4]", equalTo("DELETE"),
                        "_links.collection[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/users"))
                .extract().as(CreateUserResponse.class);

        newUserId = createUserResponse.getId();

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ROUTE + newUserId,
                        "context", "edit")
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newUserId),
                        "username", equalTo(username),
                        "name", equalTo(username),
                        "email", equalTo(email),
                        "link", equalTo("http://localhost:8000/?author=" + newUserId),
                        "nickname", equalTo(username),
                        "slug", equalTo(username.toLowerCase()),
                        "registered_date", startsWith(currentDateGmt),
                        "_links.self[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/users/" + newUserId));
    }

    @Test
    @Story("Получение пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveUserTest() {
        String username = getGeneratedString(10);
        String email = getGeneratedString(10) + "@gmail.com";
        String password = getGeneratedString(10);
        CreateUserResponse createUserResponse = createUser(username, password, email);
        int newUserId = createUserResponse.getId();

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ROUTE + newUserId,
                        "context", "edit")
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newUserId),
                        "username", equalTo(username),
                        "name", equalTo(username),
                        "email", equalTo(email),
                        "link", equalTo("http://localhost:8000/?author=" + newUserId),
                        "nickname", equalTo(username),
                        "slug", equalTo(username.toLowerCase()),
                        "_links.self[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/users/" + newUserId));
    }

    @Test
    @Story("Изменение email пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserEmailTest() {
        String username = getGeneratedString(10);
        String email = getGeneratedString(10) + "@gmail.com";
        String password = getGeneratedString(10);
        CreateUserResponse createUserResponse = createUser(username, password, email);
        int newUserId = createUserResponse.getId();
        String updatedEmail = getGeneratedString(15) + "@mail.ru";

        getBasicAuth()
                .spec(REQ_SPEC)
                .queryParams(REST_ROUTE, USERS_ROUTE + newUserId,
                        "email", updatedEmail)
                .when().post()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newUserId),
                        "email", equalTo(updatedEmail));

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ROUTE + newUserId,
                        "context", "edit")
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(newUserId),
                        "email", equalTo(updatedEmail));
    }

    @Test
    @Story("Удаление пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUserTest() {
        int userForReassignId = 0;
        int userForDeleteId = 0;
        String userForDeleteUsername = "";
        String userForDeleteEmail = "";
        for (int i = 0; i < 2; i++) {
            String username = getGeneratedString(10);
            String email = getGeneratedString(10) + "@gmail.com";
            String password = getGeneratedString(10);
            CreateUserResponse createUserResponse = createUser(username, password, email);
            if (i == 0) {
                userForReassignId = createUserResponse.getId();
            } else {
                userForDeleteId = createUserResponse.getId();
                userForDeleteUsername = username;
                userForDeleteEmail = email;
            }
        }

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ROUTE + userForDeleteId,
                        FORCE, TRUE,
                        REASSIGN, userForReassignId)
                .when().delete()
                .then()
                .statusCode(STAT_CODE_200)
                .body("deleted", equalTo(true),
                        "previous.id", equalTo(userForDeleteId),
                        "previous.username", equalTo(userForDeleteUsername),
                        "previous.email", equalTo(userForDeleteEmail));

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ROUTE + userForDeleteId,
                        FORCE, TRUE,
                        REASSIGN, userForReassignId)
                .when().delete()
                .then()
                .statusCode(STAT_CODE_404)
                .body("code", equalTo("rest_user_invalid_id"),
                        "message", equalTo("Неверный ID пользователя."),
                        "data.status", equalTo(STAT_CODE_404));
    }

    @Test
    @Story("Получение списка пользователей с сортировкой по дате регистрации")
    @Severity(SeverityLevel.CRITICAL)
    public void getListUsersTest() {
        List<Integer> userIdsList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            String username = getGeneratedString(10);
            String email = getGeneratedString(10) + "@gmail.com";
            String password = getGeneratedString(10);
            CreateUserResponse createUserResponse = createUser(username, password, email);
            userIdsList.add(createUserResponse.getId());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ROUTE,
                        "orderby", "registered_date",
                        "order", "desc",
                        "roles", "subscriber")
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("[0].id", equalTo(userIdsList.get(3)),
                        "[1].id", equalTo(userIdsList.get(2)),
                        "[2].id", equalTo(userIdsList.get(1)),
                        "[3].id", equalTo(userIdsList.get(0)));
    }

    @Test
    @Story("Получение собственного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void retrieveUserMeTest() {
        String username = getGeneratedString(10);
        String email = getGeneratedString(10) + "@gmail.com";
        String password = getGeneratedString(10);
        CreateUserResponse createUserResponse = createUser(username, password, email);
        int userMeId = createUserResponse.getId();

        getBasicAuth(username, password)
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ME_ROUTE,
                        "context", "edit")
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(userMeId),
                        "username", equalTo(username),
                        "name", equalTo(username),
                        "email", equalTo(email),
                        "link", equalTo("http://localhost:8000/?author=" + userMeId),
                        "nickname", equalTo(username),
                        "slug", equalTo(username.toLowerCase()),
                        "_links.self[0].href",
                        equalTo("http://localhost:8000/index.php?rest_route=/wp/v2/users/" + userMeId));

    }

    @Test
    @Story("Изменение никнейма собственного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserMeNicknameTest() {
        String username = getGeneratedString(10);
        String email = getGeneratedString(10) + "@gmail.com";
        String password = getGeneratedString(10);
        CreateUserResponse createUserResponse = createUser(username, password, email);
        int userMeId = createUserResponse.getId();
        String updatedNickname = getGeneratedString(15);

        getBasicAuth(username, password)
                .spec(REQ_SPEC)
                .queryParams(REST_ROUTE, USERS_ME_ROUTE,
                        "nickname", updatedNickname)
                .when().post()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(userMeId),
                        "nickname", equalTo(updatedNickname));

        getBasicAuth(username, password)
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ME_ROUTE,
                        "context", "edit")
                .when().get()
                .then()
                .statusCode(STAT_CODE_200)
                .body("id", equalTo(userMeId),
                        "nickname", equalTo(updatedNickname));
    }

    @Test
    @Story("Попытка создания пользователя с уже занятым никнеймом")
    @Severity(SeverityLevel.CRITICAL)
    public void attemptToCreateUserWithExistingUsernameTest() {
        String username = getGeneratedString(10);
        String email = getGeneratedString(10) + "@gmail.com";
        String password = getGeneratedString(10);
        createUser(username, password, email);
        CreateUserRequest createUserRequest = new CreateUserRequest(username, password, email);

        getBasicAuth()
                .spec(REQ_SPEC)
                .queryParam(REST_ROUTE, USERS_ROUTE)
                .body(createUserRequest)
                .when().post()
                .then()
                .statusCode(STAT_CODE_500)
                .body("code", equalTo("existing_user_login"),
                        "message", equalTo("Извините, это имя пользователя уже существует!"),
                        "data", nullValue());
    }

    @AfterMethod
    public void cleanUp() {
        List<CreateUserResponse> users = getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ROUTE,
                        "roles", "subscriber")
                .when().get()
                .then()
                .extract().jsonPath().getList("", CreateUserResponse.class);
        users.forEach(user -> deleteUser(user.getId()));
    }
}
