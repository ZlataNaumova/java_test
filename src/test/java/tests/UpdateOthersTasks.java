package tests;

import models.Task;
import models.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import pages.BasePage;
import pages.MainPage;

public class UpdateOthersTasks extends BaseTest {

    private static String tag1 = RandomStringUtils.randomAlphanumeric(3);
    private static String tag2 = RandomStringUtils.randomAlphanumeric(5);
    private static Task task = new Task(RandomStringUtils.randomAlphanumeric(5),
            new String[]{tag1, tag2});

    static User user = new User("NewUser", "Password");

    @BeforeClass
    public static void createTaskToEdit(){
        createWithAdminBaseTaskWithDirectRequest(task);
        createUser(user);
    }

    @Before
    public void setUp(){
        BasePage basePage = new BasePage(driver);
        basePage.login(user);
    }

    @Test
    public void testUserCannotEditOthersTask(){
        MainPage mainPage = new MainPage(driver);
        mainPage.assertTaskDoesnotHaveEditOptions(task.getTitle());
    }
}
