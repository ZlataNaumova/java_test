package tests;

import models.Task;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import pages.BasePage;
import pages.MainPage;

public class DeleteTaskTest extends BaseTest {

    private static String tag = RandomStringUtils.randomAlphanumeric(3);
    private static Task task = new Task(RandomStringUtils.randomAlphanumeric(5),
            new String[]{tag});

    @BeforeClass
    public static void createTaskToEdit(){
        createWithAdminBaseTaskWithDirectRequest(task);
    }

    @Before
    public void setUp(){
        BasePage basePage = new BasePage(driver);
        basePage.login(admin);
    }

    @Test
    public void deleteTaskTest(){
        MainPage mainPage = new MainPage(driver);
        mainPage.deleteTask(task.getTitle());
        mainPage.getTasksWithName(task.getTitle()).shouldHaveSize(0);
        mainPage.warningMessages.shouldHaveSize(0);
    }

}
