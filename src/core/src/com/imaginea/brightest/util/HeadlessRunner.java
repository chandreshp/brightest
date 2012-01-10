package com.imaginea.brightest.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imaginea.brightest.ApplicationPreferences;
import com.imaginea.brightest.junit.JUnitTestManager;
import com.imaginea.brightest.util.OptionParser.Option;
import com.imaginea.brightest.util.OptionParser.ParsingException;

/**
 * Entry method for running tests. Print help for proper usage.
 * 
 * <pre>
 * java com.imaginea.brightest.client.HeadlessRunner [options] testfile
 * </pre>
 * 
 * @author apurba
 */
public class HeadlessRunner {
    private static final Log LOGGER = LogFactory.getLog(HeadlessRunner.class);
    private final Option<String> hostName = Option.createStringOption('a', "seleniumHostAddress", "Address/hostname of the selenium server").setDefaultValue("localhost");
    private final Option<Integer> port = Option.createIntegerOption('p', "port", "Port at which selenium server is running").setDefaultValue(4444);
    private final Option<String> browser = Option.createStringOption('b', "browser", "Browser program to use").setDefaultValue("*chrome");
    private final Option<String> browserUrl = Option.createStringOption('u', "url", "Browser Url, this url is used in launching the test browser").setDefaultValue(
            "http://localhost:4444");
    private final Option<String> configFiles = Option.createStringOption('c', "config",
            "configuration file names, the names are comma seperated and determine the order of overrriding");
    private final Option<String> report = Option.createStringOption('r', "report", "Path to the Report location, must be a folder").setRequired(true);
    private final Option<Boolean> help = Option.createBooleanOption('h', "help", "Show help");
    private final OptionParser parser;

    public HeadlessRunner() {
        parser = new OptionParser(hostName, port, browser, browserUrl, configFiles, report);
    }

    public void test(String[] args) {
        parser.parse(args);
        String testPath = parser.getRemainingArg();
        if (testPath == null) {
            throw new ParsingException(args, new MissingTestFileException());
        }
        if (help.getValue() != null && help.getValue()) {
            System.err.println(getUsage());
            System.err.println("Environment variables which can control some of the features ");
            System.err.println("Environment variables which can control some of the features ");
            return;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String time = dateFormat.format(new Date());
        String reportFile = "Report" + time;
        JUnitTestManager manager = new JUnitTestManager();
        // TODO populate preferences from options directly
        ApplicationPreferences preferences = new ApplicationPreferences();
        preferences.setHost(hostName.getValue()).setPort(port.getValue()).setBrowser(browser.getValue()).setUrl(browserUrl.getValue());
        preferences.setTestPath(testPath).setReportPath(reportFile);
        TestResult result = manager.runTest(preferences);
        if (result.wasSuccessful() == false) {
            System.err.println("Run was unsucessful");
            Enumeration<TestFailure> failures = result.failures();
            while (failures.hasMoreElements()) {
                System.err.println(failures.nextElement());
            }
        }
        System.out.println("\nReport generated at " + report.getValue() + File.separator + preferences.getReportPath() + ".html");
    }

    public static void main(String args[]) {
        HeadlessRunner headlessRunner = new HeadlessRunner();
        try {
            headlessRunner.test(args);
        } catch (ParsingException exc) {
            System.err.println(exc.getMessage());
            System.err.println(headlessRunner.getUsage());
            // we need the stack trace only if we are in debug mode
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("could not parse the provided args ", exc);
            } else {
                System.err.println("For details turn logging to debug for com.imaginea packages");
            }
        }
        System.exit(0);
    }

    private String getUsage() {
        return "Usage : java " + HeadlessRunner.class.getName() + " [options] test-path \n test-path is the path to the folder or file containing the tests \n" + parser.getHelpMessage();
    }

    private static class MissingTestFileException extends RuntimeException {
        private static final long serialVersionUID = -1215488964577613361L;

        public MissingTestFileException() {
            super("Test file/folder must be passed as a argument, see usage for more details");
        }
    }
}
