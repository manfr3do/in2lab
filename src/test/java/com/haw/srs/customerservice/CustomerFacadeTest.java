package com.haw.srs.customerservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import jakarta.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CustomerFacadeTest {

    private final Log log = LogFactory.getLog(getClass());

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    @Transactional
    void setUp() {
        this.customerRepository.deleteAll();

        customer = this.customerRepository.save(new Customer("Stefan", "Sarstedt", Gender.MALE));
        // Customer customer2 = new Customer("Ulrike", "Steffens", Gender.FEMALE, "ulrike.steffens@haw-hamburg.de", new PhoneNumber("+49-40-58450583"));
        // customerRepository.save(customer2);
        RestAssured.port = port;
        RestAssured.basePath = "";
    }

    @Test
    void getAllCustomersSuccess() {
        int expected = 1;
        //@formatter:off
        var response =
                given().
                        // add this here to log request --> log().all().
                        // log().all().
                        
                when().
                        get("/customers").
                then().
                        // add this here to log response --> log().all().
                        // log().all().
                        statusCode(HttpStatus.OK.value()).
                extract().
                        response();
                        // body("lastName", hasItems("Sarstedt"));
        //@formatter:on
        List<Map<String, Object>> kunden = response.jsonPath().getList("$");
        int actual = kunden.size();
        log.info("Es wurden " + actual + " Kunden gefunden.");

        for (int i = 0; i < kunden.size(); i++) {
                Map<String, Object> k = kunden.get(i);
                log.info((i + 1) + ") " + k.get("firstName") + " " + k.get("lastName") + " (" + k.get("gender") + ")");
        }

        assertEquals(expected, actual);
    }

    @Test
    void getCustomerSuccess() {
        given().
        when().
                get("/customers/{id}", customer.getId()).
        then().
                statusCode(HttpStatus.OK.value()).
                body("firstName", equalTo(customer.getFirstName())).
                body("lastName", equalTo(customer.getLastName()));
                // body("gender", equalTo(customer.getGender()));
    }

    @Test
    void getCustomerFailBecauseOfNotFound() {
        given().
        when().
                get("/customers/{id}", Integer.MAX_VALUE).
        then().
                statusCode(HttpStatus.NOT_FOUND.value());
                // body("message", containsString("not found")); // Der 404er
    }

    @Test
    void createCustomerSuccess() {
           
        given().
                contentType(ContentType.JSON).
                body("""
                {
                "firstName": "Stefan",
                "lastName": "Sarstedt",
                "gender": "MALE"
                }
                """).
                // body(new Customer("Stefan", "Sarstedt", Gender.MALE)).
        when().
                post("/customers").
        then().
                statusCode(HttpStatus.CREATED.value()).
                body("id", is(greaterThan(0))).
                body("firstName", equalTo("Stefan")).
                body("lastName", equalTo("Sarstedt")).
                body("gender", equalTo("MALE"));
    }

    @Test
    void updateCustomerSuccess() {
    try {
        JSONObject json = new JSONObject();
        json.put("id", customer.getId());
        json.put("firstName", "Jane");
        json.put("lastName", customer.getLastName());
        json.put("gender", customer.getGender().toString());
        json.put("email", "jane.doe@example.com");
        json.put("phoneNumber", "+49-40-12345678");

        given()
        .log().all()
            .contentType(ContentType.JSON)
            .body(json.toString())
        .when()
            .put("/customers/" + customer.getId())
        .then()
        .log().all()
            .statusCode(200)
            .body("firstName", equalTo("Jane"));

    } catch (org.json.JSONException e) {
        throw new RuntimeException(e);
    }
    }

    @Test
    void deleteCustomerSuccess() {
        given().
                delete("/customers/{id}", customer.getId()).
        then().
                statusCode(HttpStatus.OK.value());

        given().
        when().
                get("/customers/{id}", customer.getId()).
        then().
                statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void createCustomerWithMissingFieldsShouldFail() {
        given()
        .contentType(ContentType.JSON)
        .body("""
        {
        "firstName": "Eve"
        }
        """)
        .when()
                .post("/customers")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

    @Test
        void createCustomerWithInvalidGenderShouldFail() {
            given()
                .contentType(ContentType.JSON)
                .body("""
                {
                "firstName": "Terminator",
                "lastName": "T-1000",
                "gender": "Cyborg"
                }
                """)
            .when()
                .post("/customers")
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value()); // JSON parse error bei falschem Enum
        }

        @Test
        void createCustomerWithLargePayload() {
        String largeString = "A".repeat(10_000); // 10k Zeichen

        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                "firstName": "%s",
                "lastName": "%s",
                "gender": "MALE"
                }
                """.formatted(largeString, largeString))
        .when()
                .post("/customers")
        .then()
                .statusCode(anyOf(is(400), is(413))); // 400 = ungültig, 413 = Payload zu groß
}
}