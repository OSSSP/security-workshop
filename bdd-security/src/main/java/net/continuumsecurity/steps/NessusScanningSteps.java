package net.continuumsecurity.steps;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.continuumsecurity.ClientFactory;
import net.continuumsecurity.Config;
import net.continuumsecurity.ReportClient;
import net.continuumsecurity.ScanClient;

import net.continuumsecurity.v5.model.Issue;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.testng.internal.Utils;

import javax.security.auth.login.LoginException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

public class NessusScanningSteps {
	Logger log = Logger.getLogger(NessusScanningSteps.class);
    ScanClient scanClient;
    ReportClient reportClient;
    String policyName;
    List<String> hostNames = new ArrayList<String>();
    String scanUuid;
    String scanIdentifierForStatus;
    String username,password;
    Map<Integer,Issue> issues;
    String nessusUrl;
    int nessusVersion;
    boolean ignoreHostNamesInSSLCert = false;

    @Given("a nessus API client that accepts all hostnames in SSL certificates")
    public void ignoreHostNamesInSSLCert() {
        ignoreHostNamesInSSLCert = true;
    }

    @Given("a nessus version $version server at $nessusUrl")
    public void createNessusClient(int version,String url) {
        nessusUrl = url;
        nessusVersion = version;
    	scanClient = ClientFactory.createScanClient(url,nessusVersion,ignoreHostNamesInSSLCert);
    }

    @Given("the nessus username $username and the password $password")
    public void setNessusCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    @Given("the scanning policy named $policyName")
    public void setScanningPolicy(String policyName) {
    	this.policyName = policyName;
    }
    
    @Given("the target hosts $hosts")
    public void setTargetHosts(ExamplesTable hostsTable) throws MalformedURLException {
    	for (Map<String,String> host : hostsTable.getRows()) {
    		String hostname = host.get("hostname");
    		if ("baseUrl".equalsIgnoreCase(hostname)) {
    			URL url = new URL(Config.getInstance().getBaseUrl());
    			hostname = url.getHost();
    		} 
    		hostNames.add(hostname);    		
    	}
    }

    @When("the scanner is run with scan name $scanName")
    public void runScan(String scanName) throws LoginException {
        if (username == null) {
            username = Config.getInstance().getNessusUsername();
            password = Config.getInstance().getNessusPassword();
        }
        scanClient.login(username,password);
        scanUuid = scanClient.newScan(scanName,policyName, Utils.join(hostNames,","));
        if (nessusVersion == 5) {
            scanIdentifierForStatus = scanName;
        } else {
            scanIdentifierForStatus = scanUuid;
        }
    }

    @When("the list of issues is stored")
    public void storeIssues() throws LoginException {
        waitForScanToComplete(scanIdentifierForStatus);
        reportClient = ClientFactory.createReportClient(nessusUrl,nessusVersion, ignoreHostNamesInSSLCert);
        reportClient.login(username,password);
        issues = reportClient.getAllIssuesSortedByPluginId(scanUuid);
    }

    @When("the following nessus false positives are removed $falsep")
    public void removeFalsePositives(ExamplesTable falsePositivesTable) {
        for (Map<String,String> row : falsePositivesTable.getRows()) {
            Integer pluginId = Integer.parseInt(row.get("PluginID"));
            String hostname = row.get("Hostname");

            Issue issue = issues.get(pluginId);
            if (issue != null) {
                issue.getHostnames().remove(hostname);
                if (issue.getHostnames().size() == 0) {
                    issues.remove(pluginId);
                }
            }
        }
    }

    @Then("no severity: $severity or higher issues should be present")
    public void verifyRiskOfIssues(int severity) {
        List<Issue> notable = new ArrayList<Issue>();
        for (Issue issue : issues.values()) {
            if (issue.getSeverity() >= severity) {
                notable.add(issue);
            }
        }
        assertThat(notable,empty());
    }

    private void waitForScanToComplete(String scanName) {
        while (scanClient.isScanRunning(scanName)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
