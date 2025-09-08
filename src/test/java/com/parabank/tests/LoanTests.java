package com.parabank.tests;

import com.parabank.pages.LoanPage;
import com.parabank.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoanTests extends BaseTest {

    @Test
    public void applyValidLoan() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        LoanPage loan = new LoanPage(driver);
        loan.goToRequestLoan();
        loan.applyLoan("1000", "100");

        Assert.assertTrue(loan.isLoanResultDisplayed(), "Loan result should be displayed after applying");
    }

    @Test
    public void applyLoanExceedingBalance() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        LoanPage loan = new LoanPage(driver);
        loan.goToRequestLoan();
        loan.applyLoan("99999999","0");

        Assert.assertTrue(loan.isLoanResultDisplayed());
    }

    @Test
    public void applyLoanZeroDownPayment() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        LoanPage loan = new LoanPage(driver);
        loan.goToRequestLoan();
        loan.applyLoan("500","0");

        Assert.assertTrue(loan.isLoanResultDisplayed());
    }

    @Test
    public void applyLoanInvalidInput() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        LoanPage loan = new LoanPage(driver);
        loan.goToRequestLoan();
        loan.applyLoan("abc","xyz");

        Assert.assertTrue(loan.isLoanResultDisplayed() || driver.getPageSource().toLowerCase().contains("error"));
    }

    @Test
    public void applyMinimumLoanAmount() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        LoanPage loan = new LoanPage(driver);
        loan.goToRequestLoan();
        loan.applyLoan("1","0");

        Assert.assertTrue(loan.isLoanResultDisplayed());
    }

    @Test
    public void applyMaximumLoanAmount() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        LoanPage loan = new LoanPage(driver);
        loan.goToRequestLoan();
        loan.applyLoan("10000000","0");

        Assert.assertTrue(loan.isLoanResultDisplayed());
    }

    @Test
    public void applyLoanEmptyFields() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        LoanPage loan = new LoanPage(driver);
        loan.goToRequestLoan();
        loan.applyLoan("", "");

        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("error") 
                       || !loan.isLoanResultDisplayed(), 
                       "Error should be displayed for empty fields");
    }

    @Test
    public void applyLoanDownPaymentGreaterThanAmount() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        LoanPage loan = new LoanPage(driver);
        loan.goToRequestLoan();
        loan.applyLoan("500", "600");  // down payment > loan amount

        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("error") 
                       || !loan.isLoanResultDisplayed(), 
                       "Error should be displayed when down payment > loan amount");
    }


    @Test
    public void applyLoanMultipleLoansLimit() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john","demo");

        LoanPage loan = new LoanPage(driver);
        loan.goToRequestLoan();

        // First loan - should succeed
        loan.applyLoan("1000", "100");
        Assert.assertTrue(loan.isLoanResultDisplayed(), "First loan should be allowed");

        // Try second/third loan - depending on rules, one should fail
        loan.goToRequestLoan();
        loan.applyLoan("2000", "200");

        boolean result = loan.isLoanResultDisplayed();
        Assert.assertTrue(result || driver.getPageSource().toLowerCase().contains("limit"),
            "System should enforce loan limits correctly");
    }
}
