package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;import java.util.ArrayList;
import java.util.List;

public class AccountsOverviewPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By accountsTable = By.id("accountTable");
    private By accountRows = By.xpath("//table[@id='accountTable']//tr[position()>1]"); 
    private By balanceCellRelative = By.xpath(".//td[2]"); 

    public AccountsOverviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(accountsTable));
        } catch (Exception ignored) {}
    }

    public boolean isAccountsOverviewPresent() {
        try {
            return !driver.findElements(accountsTable).isEmpty() && driver.findElement(accountsTable).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns first account's balance as displayed (string). Returns null if not found.
     */
    public String getFirstAccountBalance() {
        try {
            List<WebElement> rows = driver.findElements(accountRows);
            if (rows.size() == 0) {
                
                List<WebElement> alt = driver.findElements(By.xpath("//td[contains(text(),'$') or contains(text(),'₹') or contains(text,',')]"));
                if (!alt.isEmpty()) return alt.get(0).getText().trim();
                return null;
            }
            WebElement firstRow = rows.get(0);
            WebElement balanceCell = firstRow.findElement(balanceCellRelative);
            return balanceCell.getText().trim();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse a currency-like string to double; throws NumberFormatException if invalid.
     */
    public double parseCurrencyToDouble(String currencyText) {
        if (currencyText == null) throw new NumberFormatException("null");
        // Remove currency symbols and non-numeric characters except dot and minus
        String cleaned = currencyText.replaceAll("[^0-9.,\\-]", "").trim();
        // remove commass 
        cleaned = cleaned.replaceAll(",", "");
        if (cleaned.isEmpty()) throw new NumberFormatException(currencyText);
        return Double.parseDouble(cleaned);
    }

    
     // Get all balances displayed in the accounts table
     
    public List<String> getAllAccountBalances() {
        List<String> balances = new ArrayList<>();
        try {
            List<WebElement> rows = driver.findElements(accountRows);
            for (WebElement row : rows) {
                try {
                    WebElement bCell = row.findElement(balanceCellRelative);
                    balances.add(bCell.getText().trim());
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return balances;
    }
}
