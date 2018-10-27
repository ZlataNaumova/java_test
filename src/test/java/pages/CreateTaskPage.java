package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.Task;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.*;

public class CreateTaskPage extends BasePage{
    public CreateTaskPage(WebDriver webDriver) {
        super(webDriver);
    }

    Logger logger = LoggerFactory.getLogger(CreateTaskPage.class);

    public SelenideElement titleField = $("[placeholder='Task title']");
    public SelenideElement tagsField = $("[placeholder='Tags']");
    public SelenideElement closeButton = $x("//button[text()='Close']");
    public SelenideElement addButton = $x("//button[text()='Add task']");

    public CreateTaskPage load(){
        titleField.shouldBe(Condition.visible);
        tagsField.shouldBe(Condition.visible);
        return this;
    }

    public MainPage createTask(Task task){
        logger.info("Creating task '{}'", task.getTitle());
        return fillTitle(task.getTitle()).fillTags(task.getTags()).clickAddButton();
    }

    private CreateTaskPage fillTitle(String title){
        logger.info("Setting title '{}'", title);
        titleField.shouldBe(Condition.enabled);
        titleField.clear();
        titleField.setValue(title);
        return this;
    }

    private CreateTaskPage fillTags(String[] tags){
        if (!(tags.length == 0)) {
            String tagString = Stream.of(tags).collect(Collectors.joining(" "));
            logger.info("Setting tags '{}'", tagString);
            tagsField.clear();
            tagsField.setValue(tagString);
        }
        return this;
    }

    private CreateTaskPage clickCloseButton(){
        closeButton.click();
        return this;
    }

    private MainPage clickAddButton(){
        addButton.click();
        $("#add").shouldNotHave(Condition.cssClass("in"));
        return new MainPage(webDriver);
    }

}
