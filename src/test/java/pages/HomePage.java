package pages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;

public class HomePage extends BasePage {

    public HomePage(WebDriver webDriver) {
        super(webDriver);
    }

    Logger logger = LoggerFactory.getLogger(HomePage.class);

    public SelenideElement homeLink = $(By.linkText("home"));
    public SelenideElement addNewLink = $(By.linkText("add new"));



    public HomePage clickHomeLink(){
        homeLink.click();
        return this;
    }

    public CreateContactPage clickAddNewLink(){
        addNewLink.click();
        return new CreateContactPage(webDriver);
    }
}
