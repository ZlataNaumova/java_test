package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import models.User;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.*;

public class BasePage {
    public static WebDriver webDriver;
    private static WebDriverWait webDriverWait;
    private static final long DEFAULT_WAIT_TIME_OUT = 10;
    private Logger logger = LoggerFactory.getLogger(BasePage.class);


    public BasePage(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.webDriverWait = new WebDriverWait(this.webDriver, DEFAULT_WAIT_TIME_OUT);
    }

    public SelenideElement signInButton = $x("//input[@value='Login']");
    public SelenideElement usernameField = $("[name='user']");
    public SelenideElement passwordField = $("[name='pass']");
    public SelenideElement loggedInUser = $x("//*[@name='logout']/b");
    //TODO rename this
    public SelenideElement invalidCredError = $(".alert");
    public ElementsCollection warningMessages = $$(".alert").filter(Condition.visible);

    public void login(String username, String password){
        logger.info("Filling in credentials");
        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.clear();
        passwordField.sendKeys(password);
        signInButton.click();
    }

    public MainPage login(User user){
        login(user.getUsername(), user.getPassword());
        return new MainPage(webDriver);
    }

    public void dismissAlert() {
        if(invalidCredError.isDisplayed()){
            logger.info("Closing alert '{}'", invalidCredError.getText());
            invalidCredError.$("button").click();
            invalidCredError.shouldNotBe(Condition.visible);
        }
    }

    protected void setInputValueViaJS(SelenideElement element, String text) {
//        element.click();
        executeJavaScript(String.format("arguments[0].value=\"%s\";", text), element);
    }
}
