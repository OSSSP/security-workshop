package net.continuumsecurity.testng;

import net.continuumsecurity.Utils;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.steps.AppScanningSteps;
import net.continuumsecurity.steps.WebApplicationSteps;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

public class ConfigurationTest {
    protected WebApplicationSteps webAppSteps = new WebApplicationSteps();
    protected AppScanningSteps automatedScanningSteps = new AppScanningSteps();
    protected List<HashMap> usersTable;

    @AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }

    @BeforeTest
    public void beforeScenario() {
        webAppSteps.createApp();
        String workingDirectory = System.getProperty("user.dir");
        this.usersTable = Utils.createListOfMaps(workingDirectory + "/src/main/stories/users.table");
        this.automatedScanningSteps.createNewScanSession();
    }

    @Test
    public void verify_that_all_configured_user_accounts_can_login_correctly() {
        for (HashMap item : this.usersTable) {
            webAppSteps.createApp();
            webAppSteps.openLoginPage();
            webAppSteps.setUsernameFromExamples((String) item.get("username"));
            webAppSteps.setCredentialsFromExamples((String) item.get("password"));
            webAppSteps.loginWithSetCredentials();
            webAppSteps.loginSucceeds();
        }
    }

    @Test
    public void verify_that_users_are_not_logged_in_when_using_an_incorrect_password() {
        for (HashMap item : this.usersTable) {
            webAppSteps.createApp();
            webAppSteps.openLoginPage();
            webAppSteps.setUsernameFromExamples((String) item.get("username"));
            webAppSteps.incorrectPassword();
            webAppSteps.loginWithSetCredentials();
            webAppSteps.loginFails();
        }
    }

    @Test
    public void verify_that_if_users_do_not_login_then_they_are_not_logged_in_According_to_the_ILogin_isLoggedIn_Role_method() {
        webAppSteps.createApp();
        webAppSteps.openLoginPage();
        webAppSteps.loginFails();
    }

}

