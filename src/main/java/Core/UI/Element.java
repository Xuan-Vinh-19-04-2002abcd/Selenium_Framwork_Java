package Core.UI;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Element {

    private By by;

    public Element(By by) {
        this.by = by;
    }

    private WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    public WebElement waitForElementToBeVisible(int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(seconds));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            return null;
        }
    }

    public WebElement waitForElementToBeClickable(int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(seconds));
            return wait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            return null;
        }
    }

    public void waitForElementToChange(int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(seconds));
            wait.until(ExpectedConditions.stalenessOf(getDriver().findElement(by)));
        } catch (Exception e) {
            // Handle timeout exception
        }
    }

    public void click() {
        WebElement element = waitForElementToBeVisible(10);
        if (element != null) {
            element.click();
        }
    }

    public void clickByJS() {
        WebElement element = waitForElementToBeVisible(10);
        if (element != null) {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            element.click();
        }
    }

    public void enter(String value) {
        WebElement element = waitForElementToBeVisible(10);
        if (element != null) {
            element.sendKeys(value);
        }
    }

    public void select(String value) {
        WebElement element = waitForElementToBeVisible(10);
        if (element != null) {
            Select select = new Select(element);
            select.selectByVisibleText(value);
        }
    }

    public String getText() {
        WebElement element = waitForElementToBeVisible(10);
        return element != null ? element.getText().trim() : "";
    }

    public List<WebElement> findAllElements() {
        return getDriver().findElements(by);
    }

    public boolean checkElementExist() {
        List<WebElement> elements = findAllElements();
        return elements != null && !elements.isEmpty();
    }
    public boolean isDisplayed() {
        try {
            WebElement element = getDriver().findElement(by);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

}
