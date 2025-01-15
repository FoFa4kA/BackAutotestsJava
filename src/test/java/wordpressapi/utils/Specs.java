package wordpressapi.utils;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static wordpressapi.utils.PropertiesUtil.getProp;

public class Specs {

    public static RequestSpecification getBasicAuth() {
        return given().auth().preemptive().basic(getProp("username"), getProp("password"));
    }

    public static RequestSpecification getBasicAuth(String username, String password) {
        return given().auth().preemptive().basic(username, password);
    }

    public static final RequestSpecification REQ_SPEC = new RequestSpecBuilder()
            .setBaseUri(getProp("api_url"))
            .setContentType(ContentType.JSON)
            .addFilter(new AllureRestAssured())
            .build();
}
