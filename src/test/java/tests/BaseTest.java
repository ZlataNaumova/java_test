package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import models.Task;
import models.User;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;
import support.TestWatcherRule;
import org.openqa.selenium.Dimension;

import java.awt.*;
import java.util.Optional;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class BaseTest extends TestWatcherRule {

    static Logger logger = LoggerFactory.getLogger(BaseTest.class);

    public static WebDriver driver;
    protected static User admin = new User("QA", "willWin");

    @BeforeClass
    public static void beforeClass() {
        logger.info("Graphics environment info: {}", GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getBounds()
                .toString());
        Configuration.browser = Optional.ofNullable(System.getenv("BROWSER")).orElse("chrome").toLowerCase();
        Configuration.screenshots = false;
        Configuration.savePageSource = false;
        Configuration.timeout = 3000;
        WebDriverManager.chromedriver().version("2.38").setup();
        Configuration.baseUrl = "http://localhost/addressbook";
        logger.debug("base url is {}", Configuration.baseUrl);
    }

    @Before
    public void prepareBrowser() {
        Dimension HD = new Dimension(1366, 768);
        WebDriverRunner.clearBrowserCache();
        Selenide.open("/");
        driver = getWebDriver();
        new BasePage(driver);
        driver.manage().window().setPosition(new Point(1, 1));
        driver.manage().window().setSize(HD);
        logger.info("Browser size is {}", driver.manage().window().getSize().toString());
    }

    static void createWithAdminBaseTaskWithDirectRequest(Task task){
        logger.info("Creating task to edit");
        String BASE_URL = "http://shtamburg.qatest.dataiku.com/";
        given().body(task).when().auth().basic(admin.getUsername(), admin.getPassword()).contentType("application/json")
                .put(BASE_URL).then().assertThat().statusCode(200);
    }

    static void createTaskWithDirectRequest(User user, Task task){
        logger.info("Creating task '{}' with user '{}'", task.toString(), user.toString());
        String BASE_URL = "http://shtamburg.qatest.dataiku.com/";
        given().body(task).when().auth().basic(user.getUsername(), user.getPassword()).contentType("application/json")
                .put(BASE_URL).then().assertThat().statusCode(200);
    }

    static void createUser(User user) {
        logger.info("Creating user with username '{}' and password '{}'", user.getUsername(), user.getPassword());
        String BASE_URL = "http://shtamburg.qatest.dataiku.com";
        given().body(user)
                .when()
                .contentType("application/json")
                .post(BASE_URL + "/users")
                .then()
                .assertThat()
                .statusCode(200)
                .body("username", equalTo(user.getUsername()));
    }


    @AfterClass
    public static void logOut(){
        //LOGOUT
    }
}
