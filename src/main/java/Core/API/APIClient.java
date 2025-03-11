package Core.API;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

import java.util.Map;

@Getter
public class APIClient {

    private final String baseUrl;
    private final RequestSpecification request;

    public APIClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.request = RestAssured.given();
    }

    public APIClient setBasicAuthentication(String username, String password) {
        this.request.auth().preemptive().basic(username, password);
        return this;
    }

    public APIClient addDefaultHeaders(Map<String, String> headers) {
        this.request.headers(headers);
        return this;
    }

    public APIClient createRequest(String resource) {
        this.request.baseUri(this.baseUrl).basePath(resource);
        return this;
    }

    public APIClient addHeader(String key, String value) {
        this.request.header(key, value);
        return this;
    }

    public APIClient addAuthorizationHeader(String value) {
        return addHeader("Authorization", value);
    }

    public APIClient addContentTypeHeader(String value) {
        return addHeader("Content-Type", value);
    }

    public APIClient addParameter(String name, String value) {
        this.request.queryParam(name, value);
        return this;
    }

    public APIClient addBody(Object obj) {
        this.request.body(obj);
        return this;
    }

    public Response executeGet() {
        return this.request.request(Method.GET);
    }

    public <T> T executeGet(Class<T> responseType) {
        return this.request.request(Method.GET).as(responseType);
    }

    public Response executePost() {
        return this.request.request(Method.POST);
    }

    public <T> T executePost(Class<T> responseType) {
        return this.request.request(Method.POST).as(responseType);
    }

    public Response executePut() {
        return this.request.request(Method.PUT);
    }

    public <T> T executePut(Class<T> responseType) {
        return this.request.request(Method.PUT).as(responseType);
    }

    public Response executeDelete() {
        return this.request.request(Method.DELETE);
    }

    public <T> T executeDelete(Class<T> responseType) {
        return this.request.request(Method.DELETE).as(responseType);
    }

    public APIClient clearAuthentication() {
        this.request.auth().none();
        return this;
    }
}
