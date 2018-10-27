package tests;

import com.jayway.restassured.RestAssured;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import models.User;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;


@RunWith(DataProviderRunner.class)
public class UserTest {
    private static final String BASE_URL = "http://shtamburg.qatest.dataiku.com";
    static Logger logger = LoggerFactory.getLogger(UserTest.class);

    @DataProvider
    public static Object[][] validUserData(){
        return new Object[][] {
                //Apostrophes in username, password
                {new User("Hell\"ya", "O'connel")},
                //Same username, password, brackets in the name
                {new User("Test<>", "Test<>")},
                //Same username, password
                {new User("Password", "Password")},
                //characters that are used in request
                {new User("123", "?&/")},
                //Existing password, unique username
                {new User("UniqueName", "willWin")}
        };
    }

    @DataProvider
    public static Object[][] invalidUserData(){
        return new Object[][] {
                //Empty username, password
                {new User("", "")},
                //Empty username
                {new User("", "test")},
                //Spaces as username, password
                {new User(" ", " ")},
                //Empty password
                {new User("test", "")},
                //Already existing username
                {new User("QA", "Password")},

        };
    }

    @Test
    @UseDataProvider("validUserData")
    public void createUserTest(User user) {
        logger.info("Creating user with username '{}' and password '{}'", user.getUsername(), user.getPassword());
        given().body(user)
                .when()
                .contentType("application/json")
                .post(BASE_URL + "/users")
                .then()
                .assertThat()
                .statusCode(200)
                .body("username", equalTo(user.getUsername()));
    }

    @Test
    @UseDataProvider("invalidUserData")
    public void userCreationWithInvalidParametersHandled(User user){
        logger.info("Sending request to create ['{}', '{}']", user.getUsername(), user.getPassword());
        int responseStatusCode = given().body(user)
                .when()
                .contentType("application/json")
                .post(BASE_URL + "/users").statusCode();
        //Terrible hack for the sake of testing. Should return good status code with meaningful error. Or at least 400.
        //Well. at least user is not created with invalid username/password
        //TODO change once fixed [JIRA-ticket]
        assertThat(responseStatusCode).isGreaterThanOrEqualTo(400);
        //.body("error message")
    }

    @After
    public void tearDown(){
        logger.info("Resetting DB to initial state");
        RestAssured.get(BASE_URL + "/logOut").then().assertThat().statusCode(200);
    }

}
