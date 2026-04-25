package com.demo.jsondemo.utils;

import com.demo.jsondemo.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiUtils {

    static {
        RestAssured.baseURI = ConfigManager.getInstance().getBaseUrl();
    }

    public static RequestSpecification getRequest() {
        return given()
                .header("Content-Type", "application/json")
                .log().all();
    }

    public static Response get(String endpoint) {
        return getRequest()
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static Response get(String endpoint, Object pathParam) {
        return getRequest()
                .pathParam("id", pathParam)
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static Response post(String endpoint, Object body) {
        return getRequest()
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static Response put(String endpoint, Object pathParam, Object body) {
        return getRequest()
                .pathParam("id", pathParam)
                .body(body)
                .when()
                .put(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static Response delete(String endpoint, Object pathParam) {
        return getRequest()
                .pathParam("id", pathParam)
                .when()
                .delete(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }
}
