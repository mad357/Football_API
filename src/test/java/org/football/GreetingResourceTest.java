package org.football;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.apache.http.params.CoreConnectionPNames;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        RestAssured.given().port(8081).when().get("/football/hello").then().statusCode(200)
                .body(is("Hello from RESTEasy Reactive"));
    }

    @Test
    public void testHelloEndpointHttps() {
        RestAssured.given().port(8444).relaxedHTTPSValidation().when().get("https://localhost/football/hello").then().statusCode(200)
                .body(is("Hello from RESTEasy Reactive"));
    }

    @Test
    public void testConnectionToExternalSites() {
        RestAssuredConfig config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000)
                        .setParam(CoreConnectionPNames.SO_TIMEOUT, 2000));
        given().config(config).port(80).when().get("http://www.google.pl").then().statusCode(200);
    }

}