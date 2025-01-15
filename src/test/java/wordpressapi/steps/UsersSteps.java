package wordpressapi.steps;

import wordpressapi.pojos.userpojos.CreateUserRequest;
import wordpressapi.pojos.userpojos.CreateUserResponse;

import static wordpressapi.utils.Specs.REQ_SPEC;
import static wordpressapi.utils.Specs.getBasicAuth;
import static wordpressapi.utils.TestProps.FORCE;
import static wordpressapi.utils.TestProps.REASSIGN;
import static wordpressapi.utils.TestProps.REST_ROUTE;
import static wordpressapi.utils.TestProps.STAT_CODE_200;
import static wordpressapi.utils.TestProps.STAT_CODE_201;
import static wordpressapi.utils.TestProps.TRUE;
import static wordpressapi.utils.TestProps.USERS_ROUTE;

public class UsersSteps {

    public static CreateUserResponse createUser(String username, String password, String email) {
        CreateUserRequest createUserRequest = new CreateUserRequest(username, password, email);

        return getBasicAuth()
                .spec(REQ_SPEC)
                .queryParam(REST_ROUTE, USERS_ROUTE)
                .body(createUserRequest)
                .when().post()
                .then()
                .statusCode(STAT_CODE_201)
                .extract().as(CreateUserResponse.class);
    }

    public static void deleteUser(int userId) {
        getBasicAuth()
                .spec(REQ_SPEC)
                .params(REST_ROUTE, USERS_ROUTE + userId,
                        FORCE, TRUE,
                        REASSIGN, "1")
                .when().delete()
                .then()
                .statusCode(STAT_CODE_200);
    }
}
