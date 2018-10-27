package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.*;

public class UpdateTaskPage extends CreateTaskPage {
    public UpdateTaskPage(WebDriver webDriver) {
        super(webDriver);
    }

    //Probably no need to extend CreateTaskPage as elements are not the same
    SelenideElement doneCheckBox = $("[type='checkbox']");
    SelenideElement saveChangesButton = $("[data-bind='click:editTask']");
    SelenideElement titleField = $x("//div[@id='edit']//input[@placeholder='Task title']");

    public UpdateTaskPage setNewName(String newName) {
        logger.info("Setting new name '{}'", newName);
        titleField.clear();
        titleField.setValue(newName);
        titleField.shouldHave(Condition.attribute("value", newName));
        return this;
    }

    public MainPage clickSaveChanges() {
        logger.info("Saving changes");
        saveChangesButton.click();
        return new MainPage(webDriver);
    }

    private SelenideElement getTagInput(String tagName) {
        logger.info("Getting input for tag '{}'", tagName);
        $x("//div[@id='edit']//input[@id='inputTags'][1]").shouldBe(Condition.visible);
        ElementsCollection tags = $$x("//div[@id='edit']//input[@id='inputTags']");
        for (SelenideElement tag:tags) {
            if(tag.shouldBe(Condition.visible).getAttribute("value").equals(tagName)){
                return tag;
            }
        }
        throw new UnsupportedOperationException();
    }

    public MainPage updateTag(String tagToUpdate, String newTagName) {
        SelenideElement tagInputToUpdate = getTagInput(tagToUpdate);
        tagInputToUpdate.clear();
        tagInputToUpdate.setValue(newTagName);
        tagInputToUpdate.shouldHave(Condition.attribute("value", newTagName));
        saveChangesButton.click();
        return new MainPage(webDriver);
    }
}
