package tests;

import models.Task;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import pages.BasePage;
import pages.MainPage;

public class CompleteTaskTest extends BaseTest {

    private String tag1 = RandomStringUtils.randomAlphanumeric(3);
    private String tag2 = RandomStringUtils.randomAlphanumeric(5);
    private String tag3 = RandomStringUtils.randomAlphanumeric(4);
    private Task task = new Task(RandomStringUtils.randomAlphanumeric(5),
            new String[]{tag1, tag2, tag3});

    @Before
    public void setUp(){
        BasePage basePage = new BasePage(driver);
        basePage.login(admin);
        MainPage mainPage = new MainPage(driver);
        mainPage.clickAddTaskButton().createTask(task);

    }

    @Test
    public void completeTask(){
        MainPage mainPage = new MainPage(driver);
        mainPage.completeTask(task.getTitle());
        mainPage.assertThatTaskMarkedAsDone(task.getTitle()).assertTaskHasOptionToRevertToInProgress(task.getTitle());
    }

    @Test
    public void verifyTaskIsCompletedCorrectlyAfterUpdating(){
        MainPage mainPage = new MainPage(driver);
        mainPage.clickEditTask(task.getTitle()).updateTag(tag1, "");
        task.setTags(new String[]{tag2, tag3});
        mainPage.completeTask(task.getTitle())
                .assertTaskHasOptionToRevertToInProgress(task.getTitle())
                .assertThatTaskMarkedAsDone(task.getTitle())
                .assertThatTaskHasTags(task.getTitle(), new String[] {tag2, tag2});
    }
}
