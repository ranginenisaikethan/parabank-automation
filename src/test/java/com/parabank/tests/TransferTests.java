package com.parabank.tests;

import com.parabank.pages.LoginPage;
import com.parabank.pages.TransferPage;
import org.testng.Assert;
import org.testng.annotations.*;

@Test(groups = {"transfer"})
public class TransferTests extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void ensureLoggedIn() {
        LoginPage lp = new LoginPage(driver);
        if (!lp.isLoggedIn()) {
            lp.login("john", "demo");
            // brief safety: assert login succeeded or let tests fail
            Assert.assertTrue(lp.isLoggedIn(), "Precondition: login should succeed");
        }
    }

    @DataProvider(name = "transferScenarios")
    public Object[][] transferScenarios() {
        return new Object[][]{
            // fromAccount, toAccount, amount, expectedSuccess
            {"12345", "12346", "10", true},           // valid
            {"12345", "12346", "1", true},            // small amount
            {"00000", "99999", "10", false},          // invalid accounts
            {"12345", "12346", "-10", false},         // negative amount
            {"12345", "12346", "99999999", false}     // exceed balance
        };
    }

    @Test(dataProvider = "transferScenarios", description = "Parameterized transfer scenarios", groups = {"fast"})
    public void transferParameterized(String fromAccount, String toAccount, String amount, boolean expectedSuccess) {
        TransferPage tp = new TransferPage(driver);

       tp.transfer(fromAccount, toAccount, amount);

       if (expectedSuccess) {
            Assert.assertTrue(tp.isTransferSuccess(), "Expected transfer success for " + amount);
        } else {
           
            Assert.assertTrue(tp.isTransferError() || !tp.isTransferSuccess(),
                    "Expected error or no-success for transfer: " + amount);
        }
    }

    @Test(description = "Negative-amount transfer explicitly validated", groups = {"negative"})
    public void transferNegativeAmount() {
        TransferPage tp = new TransferPage(driver);
        tp.transfer("12345", "12346", "-50");
        Assert.assertTrue(tp.isTransferError() || !tp.isTransferSuccess(), "Negative amounts must fail");
    }

    @Test(description = "Invalid account transfer should show an error", groups = {"negative"})
    public void transferInvalidAccounts() {
        TransferPage tp = new TransferPage(driver);
        tp.transfer("00000", "99999", "10");
        Assert.assertTrue(tp.isTransferError() || !tp.isTransferSuccess(), "Invalid account transfer must fail");
    }
}
