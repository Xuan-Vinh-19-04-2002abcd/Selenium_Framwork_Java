import Configurations.ConfigReader;
import Core.UI.DriverManager;
import Model.Config;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class BaseTest {
    private static WebDriver driver;
    private  static final Config config = ConfigReader.getConfiguration();
    @BeforeClass
    public static void setup() {
        System.out.println("Initializing webdriver...");
        DriverManager.initDriver(config.getBrowser());
        driver = DriverManager.getDriver();

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWaitSeconds()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadSeconds()));
        driver.navigate().to(config.getHomeUrl());

        System.out.println("Webdriver initialized successfully.");
    }
    @AfterClass
    public static void teardown() {
        if (driver != null) {
           DriverManager.closeDriver();
            System.out.println("Webdriver closed.");
        }
    }
}
