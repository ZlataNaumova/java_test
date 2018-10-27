package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MainPage extends BasePage {
    public MainPage(WebDriver webDriver) {
        super(webDriver);
    }

    Logger logger = LoggerFactory.getLogger(MainPage.class);
    SelenideElement addTaskButton = $("#btn-add");

    public CreateTaskPage clickAddTaskButton() {
        logger.info("Clicking '{}' button", addTaskButton.getText());
        addTaskButton.click();
        $(".modal-open").should(Condition.visible);
        switchTo().activeElement();
        return new CreateTaskPage(webDriver).load();
    }

    public SelenideElement getTaskRowByTaskName(String taskName) {
        return $x(String.format("//tr[td[p[b[text()='%s']]]]", taskName));
    }

    public UpdateTaskPage clickEditTask(String taskName) {
        logger.info("Editing task '{}'", taskName);
        getTaskRowByTaskName(taskName).$(".glyphicon-pencil").shouldBe(Condition.enabled).click();
        switchTo().activeElement();
        return new UpdateTaskPage(webDriver);
    }

    public ElementsCollection getTasksWithName(String title) {
        return $$x(String.format("//tr[td[p[b[text()='%s']]]]", title));
    }

    public ElementsCollection getTagsForTask(String title) {
        return getTaskRowByTaskName(title).$$(".glyphicon-tag");
    }

    public MainPage deleteTask(String title) {
        getTaskRowByTaskName(title).$(".glyphicon-trash").click();
        return this;
    }

    public MainPage completeTask(String title) {
        getTaskRowByTaskName(title).$(".glyphicon-ok").click();
        return this;
    }

    public MainPage assertThatTaskMarkedAsDone(String title) {
        getTaskRowByTaskName(title).$$(".label-success").shouldHaveSize(1);
        return this;
    }

    public MainPage assertTaskHasOptionToRevertToInProgress(String title) {
        getTaskRowByTaskName(title).$$("[data-bind='click: $parent.markInProgress']")
        .shouldHaveSize(1);
        return this;
    }

    public MainPage assertThatTaskHasTags(String title, String[] tags) {
        List<String> actualTags = getTagsForTask(title).texts();
        assertThat(actualTags).hasSize(tags.length);
        assertThat(actualTags).containsExactlyInAnyOrder(tags);
        return this;
    }

    public MainPage assertTaskDoesnotHaveEditOptions(String title) {
        getTaskRowByTaskName(title).$$("[data-bind='visible: !$parent.authenticated()']").shouldHaveSize(1);
        return this;
    }
}