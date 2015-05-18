package net.continuumsecurity.testng;

import net.continuumsecurity.Constants;
import net.continuumsecurity.Utils;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.steps.AppScanningSteps;

import org.jbehave.core.model.ExamplesTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;



public class AppScanTest {
    protected AppScanningSteps automatedScanningSteps = new AppScanningSteps();
    ExamplesTable falsePositives;


    @BeforeClass
    public void beforeStory() throws Exception {
        String workingDirectory = System.getProperty("user.dir");
        falsePositives = new ExamplesTable(Utils.createStringFromJBehaveTable(workingDirectory + "/src/main/stories/tables/false_positives.table"));
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitAll();
    }

    @AfterTest
    public void after() {
        automatedScanningSteps.checkVulnerabilities("High");
        automatedScanningSteps.checkVulnerabilities("Medium");
    }

    @Test
    public void test_axss() throws Exception {
        automatedScanningSteps.createNewScanSession();
        automatedScanningSteps.navigateApp("navigate");
        automatedScanningSteps.enablePolicy(Constants.XSSPOLICY);
        automatedScanningSteps.runScanner();
        automatedScanningSteps.removeFalsePositives(falsePositives);
    }

    @Test
    public void test_sql_inj() throws Exception {
        automatedScanningSteps.createNewScanSession();
        automatedScanningSteps.navigateApp("navigate");
        automatedScanningSteps.enablePolicy(Constants.SQLINJPOLICY);
        automatedScanningSteps.runScanner();
        automatedScanningSteps.removeFalsePositives(falsePositives);
    }

}
