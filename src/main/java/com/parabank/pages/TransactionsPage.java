
package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TransactionsPage {
    private WebDriver driver;
    private By findTransactions = By.linkText("Find Transactions");
    private By fromDate = By.name("criteria.fromDate");
    private By toDate = By.name("criteria.toDate");
    private By findBtn = By.cssSelector("input[value='Find Transactions']");
    private By results = By.id("transactionTable");

    public TransactionsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void goToFindTransactions() {
        if (driver.findElements(findTransactions).size() > 0)
            driver.findElement(findTransactions).click();
    }

    public void searchByDate(String from, String to) {
        if (driver.findElements(fromDate).size() > 0) {
            driver.findElement(fromDate).clear();
            driver.findElement(fromDate).sendKeys(from);
        }
        if (driver.findElements(toDate).size() > 0) {
            driver.findElement(toDate).clear();
            driver.findElement(toDate).sendKeys(to);
        }
        if (driver.findElements(findBtn).size() > 0)
            driver.findElement(findBtn).click();
    }

    public boolean isResultPresent() {
        return driver.findElements(results).size() > 0;
    }
}
