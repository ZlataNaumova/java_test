package support;

import com.codeborne.selenide.Screenshots;
import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.codeborne.selenide.WebDriverRunner.url;



public class TestWatcherRule {

    private static final Logger logger = LoggerFactory.getLogger(TestWatcherRule.class);

    @Rule
    public TestRule testWatcher = new TestWatcher() {

        @Override
        public Statement apply(Statement base, Description description) {
            return super.apply(base, description);
        }

        @Override
        protected void starting(Description description) {
            super.starting(description);
            logger.info("Test {} started", description.getDisplayName());
        }

        @Override
        protected void succeeded(Description description) {
            logger.info("Test {} passed", description.getDisplayName());
        }

        @Override
        protected void failed(Throwable e, Description description) {
            String testName = description.getTestClass().getSimpleName() + "_" + description.getMethodName();
            logger.info("Test {} failed.", testName);
            Screenshots.takeScreenShot(testName);
            printStacktrace(e);
        }

        public void printStacktrace(Throwable e) {
            System.out.println("\n\n\n-----------------------------------------------------------------------");
            System.out.println("ATTENTION ! Below are the lines of code where the test fails");
            System.out.println("------------------------------------------------------------------------");
            System.out.println("------------------------------------------------------------------------");
            System.out.println(e.toString());
            System.out.println();
            logger.error("Failed at page {}", url());
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
            logger.info("Test {} skipped", description.getDisplayName());
        }
    };
}

