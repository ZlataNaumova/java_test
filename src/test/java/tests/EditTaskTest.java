package tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import models.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import pages.BasePage;
import pages.MainPage;
import pages.UpdateTaskPage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class EditTaskTest extends BaseTest {

    private static String tag1 = RandomStringUtils.randomAlphanumeric(3);
    private static String tag2 = RandomStringUtils.randomAlphanumeric(5);
    private static Task task = new Task(RandomStringUtils.randomAlphanumeric(5),
                new String[]{tag1, tag2});

    @Before
    public void setUp(){
        BasePage basePage = new BasePage(driver);
        basePage.login(admin);
        MainPage mainPage = new MainPage(driver);
        mainPage.clickAddTaskButton().createTask(task);
    }

    @Test
    public void editTaskName(){
        MainPage mainPage = new MainPage(driver);
        UpdateTaskPage updateTaskPage = mainPage.clickEditTask(task.getTitle());
        String newName = RandomStringUtils.randomAlphanumeric(6);
        mainPage = updateTaskPage.setNewName(newName).clickSaveChanges();
        mainPage.getTaskRowByTaskName(newName).shouldBe(Condition.visible);
    }

    @Test
    public void editTag(){
        MainPage mainPage = new MainPage(driver);
        UpdateTaskPage updateTaskPage = mainPage.clickEditTask(task.getTitle());
        String newTagName = RandomStringUtils.randomAlphanumeric(4);
        mainPage = updateTaskPage.updateTag(tag1, newTagName);
        ElementsCollection tags =
                mainPage.getTagsForTask(task.getTitle()).filter(Condition.visible).shouldHaveSize(2);
        List<String> tagNames = tags.texts();
        assertThat(tagNames.contains(newTagName));
        assertThat(tagNames.contains(tag2));
    }

    @Test
    public void editTaskSettingTwoTagsWithSameName(){
        MainPage mainPage = new MainPage(driver);
        UpdateTaskPage updateTaskPage = mainPage.clickEditTask(task.getTitle());
        mainPage = updateTaskPage.updateTag(tag1, tag2);
        ElementsCollection tags =
                mainPage.getTagsForTask(task.getTitle()).filter(Condition.visible).shouldHaveSize(1);
        List<String> tagNames = tags.texts();
        assertThat(tagNames).containsOnlyOnce(tag2);
    }

    @Test
    public void editTaskRemovingOneOfTheTags(){
        MainPage mainPage = new MainPage(driver);
        UpdateTaskPage updateTaskPage = mainPage.clickEditTask(task.getTitle());
        mainPage = updateTaskPage.updateTag(tag1, "");
        ElementsCollection tags =
                mainPage.getTagsForTask(task.getTitle()).filter(Condition.visible).shouldHaveSize(1);
        List<String> tagNames = tags.texts();
        assertThat(tagNames).containsExactly(tag2);
    }

    @Test
    public void editTaskSettingTaskNameOverTwentyChars(){
        MainPage mainPage = new MainPage(driver);
        UpdateTaskPage updateTaskPage = mainPage.clickEditTask(task.getTitle());
        mainPage = updateTaskPage.setNewName(RandomStringUtils.randomAlphanumeric(22)).clickSaveChanges();
        mainPage.getTasksWithName(task.getTitle()).shouldHaveSize(1);
        mainPage.warningMessages.shouldHaveSize(1);
    }

    @Test
    public void editTaskSettingAlreadyExistingName(){
        Task secondTask = new Task(RandomStringUtils.randomAlphanumeric(8), new String[]{tag1});
        MainPage mainPage = new MainPage(driver);
        mainPage.clickAddTaskButton().createTask(secondTask);
        mainPage.getTasksWithName(task.getTitle()).shouldHaveSize(1);
        mainPage.getTasksWithName(secondTask.getTitle()).shouldHaveSize(1);
        mainPage.clickEditTask(secondTask.getTitle()).setNewName(task.getTitle()).clickSaveChanges();
        mainPage.getTasksWithName(task.getTitle()).shouldHaveSize(1);
        mainPage.warningMessages.shouldHaveSize(1);
    }

    @After
    public void tearDown(){
        logger.info("Resetting DB to initial state");
        Selenide.open("http://shtamburg.qatest.dataiku.com/logOut");
    }

}
