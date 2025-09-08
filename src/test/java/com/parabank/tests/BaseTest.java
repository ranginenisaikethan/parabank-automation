
package com.parabank.tests;

import com.parabank.utils.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

public class BaseTest {
    protected WebDriver driver;
    protected String baseUrl = "https://parabank.parasoft.com/parabank/index.htm";

    @Parameters({ "browser" })
    @BeforeMethod(alwaysRun = true)
    public void setUp(String browser) {
        driver = WebDriverFactory.createDriver(browser);
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
