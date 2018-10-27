package tests;

import com.codeborne.selenide.Condition;
import models.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;

import java.util.ArrayList;
import java.util.List;

public class LoginTest extends BaseTest {

    Logger logger = LoggerFactory.getLogger(LoginTest.class);

    @Test
    public void loginTest(){

        User validUser = new User("admin", "secret");
        BasePage basePage = new BasePage(driver);
        basePage.login(validUser.getUsername(), validUser.getPassword());
        basePage.loggedInUser.shouldHave(Condition.text(validUser.getUsername()));
    }

    @Test
    public void loginWithInvalidCredentials(){
        List<User> users = new ArrayList<>();
        users.add(new User("admin", "admin"));
        users.add(new User("admin", "root"));
        users.add(new User("", ""));
        users.add(new User("test", "willWin"));
        users.add(new User("QA", "test"));
        users.add(new User("QA", ""));
        users.add(new User("", "willWin"));
        users.add(new User("<script>alert('XSS')</script>", "test"));

        BasePage basePage = new BasePage(driver);
        //We need to run test against set of data. We use softly assert in order to run all scenarios and then validate
        //which ones has failed. Otherwise test will fail on the first unexpected result and won't run rest
        SoftAssertions softly = new SoftAssertions();
        for (User user:users) {
            logger.info("Trying to login with '{}', '{}'", user.getUsername(), user.getPassword());
            basePage.login(user);
            softly.assertThat(basePage.invalidCredError.shouldBe(Condition.visible));
            softly.assertThat(basePage.invalidCredError.getText()).contains("Your login is invalid or session has expired");
            softly.assertThat(basePage.warningMessages.shouldHaveSize(1));
            basePage.dismissAlert();
            softly.assertAll();
        }
    }
}
