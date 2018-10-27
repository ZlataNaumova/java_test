package tests;

import com.codeborne.selenide.Condition;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import models.Task;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import pages.BasePage;
import pages.CreateTaskPage;
import pages.MainPage;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(DataProviderRunner.class)
public class CreateTaskTest extends BaseTest {

    @Before
    public void login(){
        BasePage basePage = new BasePage(driver);
        basePage.login(admin);
    }

    @DataProvider
    public static Object[][] validDataProvider() {
        String textTag = (RandomStringUtils.randomAlphabetic(5));
        String numericTag = (RandomStringUtils.randomNumeric(10));
        String specialCharactersTag = (RandomStringUtils.random(8, "|!@#$%^&*()_+~?<>.,/\\]"));
        String emptyTag = "";
        return new Object[][] {
                //Text task name and empty tag + tag with text tag name
                {new Task(RandomStringUtils.randomAlphanumeric(8), new String[]{emptyTag, textTag})},
                //Text task name and text tag name
                {new Task(RandomStringUtils.randomAlphabetic(6), new String[]{textTag})},
                //Numeric task name and numeric tag name
                {new Task(RandomStringUtils.randomNumeric(3), new String[]{numericTag})},
                //Special characters task name and special characters tag name
                {new Task(RandomStringUtils.random(4, "|!@#$%^&*()_+~?<>.,/]"),
                        new String[]{specialCharactersTag})},
                //Special characters task name and special char tag + text tag name
                {new Task(RandomStringUtils.random(4, "|!@#$%^&*()_+~?<>.,/]"), new String[]{textTag})},
                //Numeric task name and text tag + special char tag name
                {new Task(RandomStringUtils.randomNumeric(3),
                        new String[]{textTag, specialCharactersTag})},
                //Mixed task name and special char + text + numeric tag name
                {new Task(RandomStringUtils.randomAlphanumeric(7).
                        concat(RandomStringUtils.random(4, "|!@#$%^&*()_+~?<>.,/\\]")),
                        new String[]{specialCharactersTag, textTag, numericTag})},
                //Alphanumeric task name and empty tag name
                {new Task(RandomStringUtils.randomAlphanumeric(6),
                        new String[]{emptyTag})},
                //Empty task name and empty tag name
                {new Task("", new String[]{emptyTag})},
                //Empty task name and numeric + special char tag name
                {new Task("", new String[]{numericTag, specialCharactersTag})},
                //Task name of length 20 with space in the end(Should be trimmed), valid tag
                {new Task(RandomStringUtils.randomAlphanumeric(20)+" ", new String[]{textTag})},
                //Task name with space in the middle, valid tag
                {new Task(RandomStringUtils.randomAlphanumeric(3) + " " + RandomStringUtils.randomAlphanumeric(5),
                        new String[]{textTag})}

        };
    }

    @DataProvider
    public static Object[][] invalidDataProvider() {
        String validTag = RandomStringUtils.randomAlphanumeric(8);
        String longTextTag = RandomStringUtils.randomAlphanumeric(25);
        return new Object[][] {
                //Over 20 char task name, textTag
                {new Task(RandomStringUtils.randomAlphanumeric(21), new String[]{validTag})},
                //Empty task name and text tag name
                {new Task("", new String[]{validTag})},
                //Valid task name, over 20 char tag name
                {new Task(RandomStringUtils.randomAlphanumeric(6), new String[]{longTextTag})},
                //Over 20 char task name, over 20 char tag name
                {new Task(RandomStringUtils.randomAlphanumeric(23), new String[]{longTextTag})},
                //Valid task name, valid + invalid tag
                {new Task(RandomStringUtils.randomAlphanumeric(8), new String[]{validTag, longTextTag})},
                //Space as task name, valid tag
                {new Task(" ", new String[]{validTag})}
        };
    }

    @Test
    @UseDataProvider("validDataProvider")
    public void createTaskWithValidTitleAndTag(Task task){
        MainPage mainPage = new MainPage(driver);
        CreateTaskPage createTaskPage = mainPage.clickAddTaskButton();
        mainPage = createTaskPage.createTask(task);
        //assert task is on the page
        mainPage.getTaskRowByTaskName(task.getTitle().trim())
                .shouldBe(Condition.exist
                .because(String.format("Task '%s' with tags '%s' is not on the list",
                        task.getTitle(), Arrays.toString(task.getTags()))));
        //assert that if task is on the list, there are no error messages on the page
        mainPage.warningMessages.filter(Condition.visible).shouldHaveSize(0);
    }

    @Test
    @UseDataProvider("invalidDataProvider")
    public void createTaskWithInvalidTitleOrTag(Task task){
        MainPage mainPage = new MainPage(driver);
        CreateTaskPage createTaskPage = mainPage.clickAddTaskButton();
        mainPage = createTaskPage.createTask(task);
        //assert Task wasn't added to the list
        mainPage.getTasksWithName(task.getTitle()).shouldHave(size(0)
                .because(String.format("Task {%s} should not appear on the page", task)));
        //assert only one error message is present
        mainPage.warningMessages.shouldHave(size(1)
                .because(String.format("Error message should appear while trying to create task {%s}", task)));
        mainPage.dismissAlert();
    }

    @Test
    public void createTaskWithAlreadyExistingName() {
        //Preconditions: task exists
        String tag = RandomStringUtils.randomAlphanumeric(6);
        Task task = new Task(RandomStringUtils.randomAlphanumeric(8), new String[]{tag});
        createTaskWithValidTitleAndTag(task);
        MainPage mainPage = new MainPage(driver);
        //Test
        CreateTaskPage createTaskPage = mainPage.clickAddTaskButton();
        mainPage = createTaskPage.createTask(task);
        //assert only one task on the page
        mainPage.getTasksWithName(task.getTitle()).shouldHaveSize(1);
        //assert error one error message appears
        mainPage.warningMessages.filter(Condition.visible).shouldHaveSize(1);
    }

    @Test
    public void createTaskAddingTwoTagsWithTheSameName(){
        String tag = RandomStringUtils.randomAlphanumeric(5);
        Task task = new Task(RandomStringUtils.randomAlphanumeric(7), new String[]{tag, tag});
        createTaskWithValidTitleAndTag(task);
        MainPage mainPage = new MainPage(driver);
        //assert only one tag on the page
        mainPage.getTagsForTask(task.getTitle()).shouldHaveSize(1);
        List<String> tagsFromPage = mainPage.getTagsForTask(task.getTitle()).texts();
        //assert tag name is expected
        assertThat(tagsFromPage).containsOnlyOnce(tag);
    }

}
