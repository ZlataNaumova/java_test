package tests;

import com.jayway.restassured.RestAssured;
import models.Task;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import static com.jayway.restassured.RestAssured.given;
import static tests.BaseTest.admin;
import static tests.BaseTest.createTaskWithDirectRequest;
import static tests.BaseTest.createUser;

public class PermissionsTest {

    private static final String BASE_URL = "http://shtamburg.qatest.dataiku.com/";
    static Logger logger = LoggerFactory.getLogger(PermissionsTest.class);
    String taskName = RandomStringUtils.randomAlphanumeric(5);
    String tagName = RandomStringUtils.randomAlphanumeric(3);
    Task task = new Task(taskName, new String[]{tagName});
    String username = RandomStringUtils.randomAlphabetic(5);
    String password = RandomStringUtils.randomAlphanumeric(8);
    User user = new User(username, password);

    @Before
    public void setUp(){
        logger.info("Creating user '{}'", user.toString());
        createUser(user);
        createTaskWithDirectRequest(user, task);
    }

    @Test
    public void deleteOwnTask(){
        String id = extractId();
        given().when().auth().basic(user.getUsername(), user.getPassword()).delete(BASE_URL + id).then()
                .assertThat().statusCode(200);
    }

    @Test
    public void deleteOthersTask(){
        String id = extractId();
        given().when().auth().basic(admin.getUsername(), admin.getPassword()).delete(BASE_URL + id).then()
                .assertThat().statusCode(403);
    }

    @Test
    public void deleteWithoutAuth(){
        String id = extractId();
        given().when().delete(BASE_URL + id).then()
                .assertThat().statusCode(401);
    }

    @Test
    public void patchOwnTask(){
        String updatedTaskName = RandomStringUtils.randomAlphanumeric(15);
        String updatedTag = RandomStringUtils.randomAlphanumeric(8);
        Task updatedTask = new Task(updatedTaskName, new String[]{updatedTag});
        logger.info("Updating task '{}' to '{}", task, updatedTask);
        String id = extractId();
        given().when().auth().basic(user.getUsername(), user.getPassword()).body(updatedTask)
                .patch(BASE_URL + id).then()
                .assertThat().statusCode(200);
    }

    @Test
    public void patchOthersTask(){
        String updatedTaskName = RandomStringUtils.randomAlphanumeric(15);
        String updatedTag = RandomStringUtils.randomAlphanumeric(8);
        Task updatedTask = new Task(updatedTaskName, new String[]{updatedTag});
        String id = extractId();
        given().when().auth().basic(admin.getUsername(), admin.getPassword()).body(updatedTask)
                .patch(BASE_URL + id).then()
                .assertThat().statusCode(403);
    }

    @Test
    public void patchWithoutAuth(){
        String updatedTaskName = RandomStringUtils.randomAlphanumeric(15);
        String updatedTag = RandomStringUtils.randomAlphanumeric(8);
        Task updatedTask = new Task(updatedTaskName, new String[]{updatedTag});
        String id = extractId();
        given().when().body(updatedTask)
                .patch(BASE_URL + id).then()
                .assertThat().statusCode(401);
    }

    private String extractId(){
        String id = given().when().get(BASE_URL).then().statusCode(200).extract().jsonPath().getString("[0].id");
        logger.info("Extracted id is '{}'", id);
        return id;
    }

    @After
    public void tearDown(){
        logger.info("Resetting DB to initial state");
        RestAssured.get(BASE_URL + "logOut").then().assertThat().statusCode(200);
    }
}
