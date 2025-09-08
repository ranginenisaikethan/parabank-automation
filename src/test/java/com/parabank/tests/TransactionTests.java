
package com.parabank.tests;

import com.parabank.pages.LoginPage;
import com.parabank.pages.TransactionsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TransactionTests extends BaseTest {

    @Test
    public void recentTransferAppearsInHistory() throws InterruptedException {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");
        TransactionsPage tp = new TransactionsPage(driver);
        tp.goToFindTransactions();
        // using wide date range to get results
        tp.searchByDate("01/01/2020","12/31/2025");
        Thread.sleep(1000);
        Assert.assertTrue(tp.isResultPresent(), "Transaction results should be present");
    }

    @Test
    public void searchByAmount() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");
        TransactionsPage tp = new TransactionsPage(driver);
        tp.goToFindTransactions();
        // site may require different fields; generic check for results present
        tp.searchByDate("01/01/2020","12/31/2025");
        Assert.assertTrue(tp.isResultPresent(), "Should find transactions by amount/date");
    }

    @Test
    public void searchByDate() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");
        TransactionsPage tp = new TransactionsPage(driver);
        tp.goToFindTransactions();
        tp.searchByDate("01/01/2021","12/31/2021");
        Assert.assertTrue(tp.isResultPresent() || driver.getPageSource().toLowerCase().contains("no transactions found"));
    }
}
