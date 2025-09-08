package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoanPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators - these are generic guesses for ParaBank; change if your page uses different attributes.
    private By requestLoanLink = By.linkText("Request Loan");         // nav link text
    private By loanAmountInput = By.id("amount");                     // loan amount field
    private By downPaymentInput = By.id("downPayment");               // down payment field
    private By fromAccountSelect = By.id("fromAccountId");           // account selector (if any)
    private By applyButton = By.xpath("//input[@value='Apply Now' or @type='submit']"); // apply button
    private By loanResultSection = By.id("loanResults");              // possible result container
    private By loanResultMessage = By.xpath("//*[contains(text(),'Congratulations') or contains(.,'Approved') or contains(.,'denied') or contains(.,'processed')]");

    public LoanPage(WebDriver driver) {
        this.driver = driver;
        // 10s default wait; adjust per needs
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Navigate to the Request Loan page/form.
     * Waits for the form inputs to be visible.
     */
    public void goToRequestLoan() {
        // If link exists in nav use it
        try {
            wait.until(ExpectedConditions.elementToBeClickable(requestLoanLink)).click();
        } catch (Exception e) {
            // fallback: try to open via URL fragment if your site uses a known path
            // driver.get("https://parabank.parasoft.com/parabank/requestloan.htm");
            // If none work, rethrow so test fails with clear error
            throw new RuntimeException("Unable to find/click 'Request Loan' link. Locator may be wrong.", e);
        }

        // wait for loan form to appear (either amount or apply button)
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(loanAmountInput),
                ExpectedConditions.visibilityOfElementLocated(applyButton)
        ));
    }

    /**
     * Fill and submit the loan application. If there's a from-account dropdown, pick the first option by default.
     */
    public void applyLoan(String amount, String downPayment) {
        // wait for inputs to be present
        wait.until(ExpectedConditions.visibilityOfElementLocated(loanAmountInput));
        WebElement amountEl = driver.findElement(loanAmountInput);
        amountEl.clear();
        amountEl.sendKeys(amount);

        wait.until(ExpectedConditions.visibilityOfElementLocated(downPaymentInput));
        WebElement downEl = driver.findElement(downPaymentInput);
        downEl.clear();
        downEl.sendKeys(downPayment);

        // If there's an account select, optionally pick first
        try {
            if (!driver.findElements(fromAccountSelect).isEmpty()) {
                WebElement accountSelect = driver.findElement(fromAccountSelect);
                // try selecting first option by clicking
                List<WebElement> options = accountSelect.findElements(By.tagName("option"));
                if (options.size() > 0) {
                    options.get(0).click();
                }
            }
        } catch (Exception ignored) {
        }

        // submit
        wait.until(ExpectedConditions.elementToBeClickable(applyButton)).click();

        // wait for either loan result or an error message on page
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(loanResultMessage),
                ExpectedConditions.visibilityOfElementLocated(loanResultSection),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'error') or contains(text(),'Error') or contains(.,'You do not have the funds')]"))
        ));
    }

    /**
     * Check if a loan result (approval/denial/processed) is displayed.
     */
    public boolean isLoanResultDisplayed() {
        try {
            // prefer explicit high-level messages
            if (!driver.findElements(loanResultMessage).isEmpty()) {
                return driver.findElement(loanResultMessage).isDisplayed();
            }
            if (!driver.findElements(loanResultSection).isEmpty()) {
                return driver.findElement(loanResultSection).isDisplayed();
            }
            // fallback: search for typical success words in the page
            String src = driver.getPageSource().toLowerCase();
            return src.contains("congratulations") || src.contains("approved") || src.contains("processed") || src.contains("denied");
        } catch (Exception e) {
            return false;
        }
    }
}
