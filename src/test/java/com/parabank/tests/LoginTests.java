package com.parabank.tests;

import com.parabank.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @Test
    public void validLogin() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john", "demo");
        Assert.assertTrue(lp.isLoggedIn(), "Should be logged in");
        lp.logout();
    }

    @Test
    public void invalidLoginWrongPassword() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john", "wrongpass");
        Assert.assertTrue(lp.isLoginError(), "Error should be displayed");
    }

    @Test
    public void invalidLoginWrongUser() {
        LoginPage lp = new LoginPage(driver);
        lp.login("nouser", "demo");
        Assert.assertTrue(lp.isLoginError(), "Error should be displayed");
    }

    @Test
    public void emptyCredentials() {
        LoginPage lp = new LoginPage(driver);
        lp.login("", "");
        Assert.assertTrue(lp.isLoginError(), "Error should be displayed");
    }

    @Test
    public void multipleInvalidAttempts() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john", "wrong1");
        lp.login("john", "wrong2");
        lp.login("john", "wrong3");
        Assert.assertTrue(lp.isLoginError(), "Error should be displayed after multiple attempts");
    }

    @Test
    public void validLoginWithUppercaseUsername() {
        LoginPage lp = new LoginPage(driver);
        lp.login("JOHN", "demo");  // uppercase username
        Assert.assertFalse(lp.isLoggedIn(), "Uppercase username should not log in (reflects current behavior)");
        if (lp.isLoggedIn()) {
            lp.logout();
        }
    }

    @Test
    public void validLoginWithUppercasePassword() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john", "DEMO");  // uppercase password
        Assert.assertFalse(lp.isLoggedIn(), "Uppercase password should not log in (reflects current behavior)");
        if (lp.isLoggedIn()) {
            lp.logout();
        }
    }

    @Test
    public void loginWithoutPassword() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john", "");  // no password
        Assert.assertTrue(lp.isLoginError(), "Error should be displayed if password is missing");
    }

    @Test
    public void loginWithoutUsername() {
        LoginPage lp = new LoginPage(driver);
        lp.login("", "demo");  // no username
        Assert.assertTrue(lp.isLoginError(), "Error should be displayed if username is missing");
    }

    @Test
    public void logoutAfterLogin() {
        LoginPage lp = new LoginPage(driver);
        lp.login("john", "demo");
        Assert.assertTrue(lp.isLoggedIn(), "Should be logged in");
        lp.logout();
        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("customer login"), "Should be logged out");
    }
}
