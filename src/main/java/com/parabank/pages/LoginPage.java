package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Restored / stable LoginPage for your LoginTests.
 * - Uses explicit waits so login actions finish before assertions run.
 * - Keeps main-window switching (like your previous code).
 * - Exposes isLoggedIn(), isLoginError(), logout() used by tests.
 */
public class LoginPage {
    private WebDriver driver;
    private String mainWindow;
    private WebDriverWait wait;

    private By username = By.name("username");
    private By password = By.name("password");
    private By loginBtn = By.cssSelector("input[value='Log In']");
    private By logoutLink = By.linkText("Log Out");
    private By accountsOverviewLink = By.linkText("Accounts Overview");
    private By loginFormUsername = By.name("username"); // used to detect login form presence
    private By errorMessage = By.xpath("//*[contains(@class,'error') or contains(translate(., 'ERROR','error'), 'error') or contains(.,'was not found') or contains(.,'invalid')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.mainWindow = driver.getWindowHandle();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    // Ensure we are always on the main window (keeps your prior behavior)
    private void switchToMainWindow() {
        try {
            driver.switchTo().window(mainWindow);
        } catch (Exception ignored) {}
    }

    /** Perform login and wait briefly for post-login indicator (or error). */
    public void login(String user, String pass) {
        switchToMainWindow();
        // wait for login form to be visible (if it's present)
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormUsername));
        } catch (Exception ignored) {}

        driver.findElement(username).clear();
        driver.findElement(username).sendKeys(user);

        driver.findElement(password).clear();
        driver.findElement(password).sendKeys(pass);

        driver.findElement(loginBtn).click();

        // After clicking, wait for either a post-login indicator or an error (short timeout)
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(accountsOverviewLink),
                ExpectedConditions.visibilityOfElementLocated(logoutLink),
                ExpectedConditions.visibilityOfElementLocated(errorMessage)
            ));
        } catch (Exception ignored) {
            // swallow - tests will assert the specific conditions
        }
    }

    /** True if logout link or Accounts Overview link is present (indicates logged in). */
    public boolean isLoggedIn() {
        switchToMainWindow();
        try {
            return (!driver.findElements(logoutLink).isEmpty() && driver.findElement(logoutLink).isDisplayed())
                    || (!driver.findElements(accountsOverviewLink).isEmpty() && driver.findElement(accountsOverviewLink).isDisplayed());
        } catch (Exception e) {
            return false;
        }
    }

    /** True if a login error message appears or page contains typical error text. */
    public boolean isLoginError() {
        switchToMainWindow();
        try {
            if (!driver.findElements(errorMessage).isEmpty() && driver.findElement(errorMessage).isDisplayed()) {
                return true;
            }
            String src = driver.getPageSource().toLowerCase();
            return src.contains("error") || src.contains("invalid") || src.contains("invalid username or password") || src.contains("was not found");
        } catch (Exception e) {
            return false;
        }
    }

    /** Click logout if present, then wait for login form to reappear. */
    public void logout() {
        switchToMainWindow();
        try {
            if (!driver.findElements(logoutLink).isEmpty()) {
                WebElement link = driver.findElement(logoutLink);
                // Remove target attribute if it would open new window (keeps behavior from your current class)
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('target');", link);
                } catch (Exception ignored) {}
                link.click();
                // wait until login form visible again
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormUsername));
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
    }
}
