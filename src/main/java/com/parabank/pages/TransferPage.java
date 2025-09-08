package com.parabank.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class TransferPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // navigation
    private By navTransfer = By.linkText("Transfer Funds"); // common on ParaBank demo
    // form fields
    private By amountInput = By.id("amount");
    private By fromSelect = By.id("fromAccountId");
    private By toSelect = By.id("toAccountId");
    private By transferBtn = By.cssSelector("input[type='submit'][value='Transfer'], button[type='submit']");
    // success / error detection (multiple fallbacks)
    private By successMsg = By.xpath("//*[contains(text(),'Transfer Complete') or contains(text(),'Transfer successful') or contains(.,'was successfully') or contains(.,'Transfer Complete')]");
    private By errorMsg = By.xpath("//*[contains(@class,'error') or contains(translate(., 'ERROR','error'),'error') or contains(.,'You do not have the funds') or contains(.,'Insufficient')]");

    public TransferPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Ensure we are on the transfer page. If transfer form present already, do nothing.
    private void openIfNotOpen() {
        try {
            if (!driver.findElements(amountInput).isEmpty()) return;
            if (!driver.findElements(navTransfer).isEmpty()) {
                wait.until(ExpectedConditions.elementToBeClickable(navTransfer)).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput));
                return;
            }
            // fallback: try known path
            driver.get("https://parabank.parasoft.com/parabank/transfer.htm");
            wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput));
        } catch (Exception e) {
            throw new RuntimeException("Unable to open Transfer page", e);
        }
    }

    public void transfer(String fromAccount, String toAccount, String amount) {
        openIfNotOpen();

        WebElement amountEl = wait.until(ExpectedConditions.elementToBeClickable(amountInput));
        amountEl.clear();
        amountEl.sendKeys(amount);

        // select from account
        try {
            if (!driver.findElements(fromSelect).isEmpty()) {
                Select s = new Select(driver.findElement(fromSelect));
                selectByValueOrText(s, fromAccount);
            } else {
                fillIfPresent(By.name("fromAccount"), fromAccount);
            }
        } catch (Exception ignored) {}

        // select to account
        try {
            if (!driver.findElements(toSelect).isEmpty()) {
                Select s = new Select(driver.findElement(toSelect));
                selectByValueOrText(s, toAccount);
            } else {
                fillIfPresent(By.name("toAccount"), toAccount);
            }
        } catch (Exception ignored) {}

        // submit
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(transferBtn));
            btn.click();
        } catch (TimeoutException te) {
            // fallback: submit form via amount input
            try { amountEl.submit(); } catch (Exception e) { throw new RuntimeException("Cannot click transfer button", e); }
        }

        // wait for either success OR error OR generic Transfer text
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(successMsg),
                ExpectedConditions.visibilityOfElementLocated(errorMsg),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(.,'Transfer') and (contains(.,'Complete') or contains(.,'Error') or contains(.,'successful'))]"))
        ));
    }

    private void selectByValueOrText(Select sel, String val) {
        try { sel.selectByValue(val); }
        catch (NoSuchElementException e1) {
            try { sel.selectByVisibleText(val); }
            catch (NoSuchElementException e2) {
                List<WebElement> opts = sel.getOptions();
                if (!opts.isEmpty()) opts.get(0).click();
            }
        }
    }

    private void fillIfPresent(By locator, String text) {
        List<WebElement> els = driver.findElements(locator);
        if (!els.isEmpty()) {
            WebElement el = els.get(0);
            el.clear();
            el.sendKeys(text);
        }
    }

    public boolean isTransferSuccess() {
        try {
            return !driver.findElements(successMsg).isEmpty() && driver.findElement(successMsg).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTransferError() {
        try {
            if (!driver.findElements(errorMsg).isEmpty() && driver.findElement(errorMsg).isDisplayed()) return true;
            String src = driver.getPageSource().toLowerCase();
            return src.contains("error") || src.contains("insufficient") || src.contains("you do not have the funds");
        } catch (Exception e) {
            return false;
        }
    }
}
