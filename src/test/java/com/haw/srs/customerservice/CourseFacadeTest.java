package com.haw.srs.customerservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseFacadeTest {

    private final Log log = LogFactory.getLog(getClass());

    @LocalServerPort
    private int port;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Course course;

    @BeforeEach
    @Transactional
    void setUp() {
        // List<Customer> customers = customerRepository.findAll();

        // for (Customer c : customers) {
        //         for (Course course : c.getCourses()) {
        //                 course.getCustomers().remove(c);
        //         }
        //     c.getCourses().clear();
        // }

        // courseRepository.flush();
        // customerRepository.flush();

        customerRepository.deleteAll();
        courseRepository.deleteAll();

        course = this.courseRepository.save(new Course("Mathe mit Kluock"));

        RestAssured.port = port;
        RestAssured.basePath = "";
    }

    @Test
    void getAllCoursesSuccess() {
        int expected = 1;
        var response =
                given().
                        // add this here to log request --> log().all().
                        // log().all().
                        
                when().
                        get("/courses").
                then().
                        // add this here to log response --> log().all().
                        // log().all().
                        statusCode(HttpStatus.OK.value()).
                extract().
                        response();
                        // body("lastName", hasItems("Sarstedt"));
        //@formatter:on
        List<Map<String, Object>> kurse = response.jsonPath().getList("$");
        int actual = kurse.size();
        log.info("Es wurden " + actual + " Kurse gefunden.");

        for (int i = 0; i < kurse.size(); i++) {
                Map<String, Object> k = kurse.get(i);
                log.info((i + 1) + ") " + k.get("name") + " " + k.get("teilnehmer") + ")");
        }

        assertEquals(expected, actual);
    }

    @Test
    void getCourseSuccess() {
        given().
        when().
                get("/courses/{id}", course.getId()).
        then().
                statusCode(HttpStatus.OK.value()).
                body("name", equalTo(course.getName()));
    }

    @Test
    void getCourseFailBecauseOfNotFound() {
        given().
        when().
                get("/corse/{id}", Integer.MAX_VALUE).
        then().
                statusCode(HttpStatus.NOT_FOUND.value());
                // body("message", containsString("not found")); // Der 404er
    }

    @Test
    void createCourseSuccess() {
        given().
                contentType(ContentType.JSON).
                body(new Course("TestKurs")).
        when().
                post("/courses").
        then().
                statusCode(HttpStatus.CREATED.value()).
                body("id", is(greaterThan(0))).
                body("name", equalTo("TestKurs"));
    }

    @Test
    void updateCourseSuccess() {
    try {
        JSONObject json = new JSONObject();
        json.put("id", course.getId());
        json.put("name", "UpdatedCourse");

        given()
        .log().all()
            .contentType(ContentType.JSON)
            .body(json.toString())
        .when()
            .put("/courses/" + course.getId())
        .then()
        .log().all()
            .statusCode(200)
            .body("name", equalTo("UpdatedCourse"));

        } catch (org.json.JSONException e) {
                throw new RuntimeException(e);
        }
    }

        @Test
        void deleteCourseSuccess() {
        given().
                log().all().
                delete("/courses/{id}", course.getId()).
        then().
                statusCode(HttpStatus.OK.value());

        given().
        when().
                get("/courses/{id}", course.getId()).
        then().
                log().all().
                statusCode(HttpStatus.NOT_FOUND.value());
    }
}