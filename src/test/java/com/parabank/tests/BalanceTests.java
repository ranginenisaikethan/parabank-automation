package com.parabank.tests;

import com.parabank.pages.AccountsOverviewPage;
import com.parabank.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BalanceTests extends BaseTest {

    
    // Test 1: Verify Accounts Overview is visible after login
     
    @Test
    public void balanceAfterDeposit() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        AccountsOverviewPage ap = new AccountsOverviewPage(driver);
        Assert.assertTrue(ap.isAccountsOverviewPresent(), 
            "Accounts overview should be present after login");
    }

}
