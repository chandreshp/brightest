package com.imaginea.brightest.junit;

import java.io.File;

import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;

import com.imaginea.brightest.ApplicationPreferences;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.format.UnknownFormatException;
import com.imaginea.brightest.util.Util;

/**
 * Adapter to create a testsuite from a test group
 */
@RunWith(JUnitTestRunner.class)
public class JUnitTestSuiteAdapter extends TestSuite {
    private static final Log LOG = LogFactory.getLog(JUnitTestSuiteAdapter.class);
    private static int suiteCounter = 1;

    public JUnitTestSuiteAdapter() {
        this(false);
    }

    public JUnitTestSuiteAdapter(boolean discover) {
        init(discover);
    }

    private void init(boolean discover) {
        if (discover) {
            ApplicationPreferences preferences = ExecutionContext.getInstance().getPreferences();
            String testFolderOrFile = System.getProperty("brighTest.path");
            testFolderOrFile = (Util.isNotBlank(testFolderOrFile)) ? testFolderOrFile : preferences.getTestPath();
            if (Util.isNotBlank(testFolderOrFile)) {
                discoverAndAddTests(testFolderOrFile);
            }
            setName(testFolderOrFile);
        } else {
            setName(this.getClass().getSimpleName() + "_" + suiteCounter++);
        }
    }

    private void discoverAndAddTests(String testFolderOrFile) {
        JUnitTestManager mgr = new JUnitTestManager();
        File file = new File(testFolderOrFile);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File testFile : files) {
                try {
                    TestSuite suite = mgr.loadSuite(testFile.getAbsolutePath());
                    for (int i = 0; i < suite.testCount(); i++) {
                        this.addTest(suite.testAt(i));
                    }
                } catch (UnknownFormatException exc) {
                    LOG.warn("Format of file '" + testFile.getAbsolutePath() + "' is not understandable");
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Unknown format file", exc);
                    }
                }
            }
        } else {
            this.addTest(mgr.loadSuite(testFolderOrFile));
        }
    }

    public String toString() {
        return String.format("%s with %d tests", this.getClass().getSimpleName(), testCount());
    }
}
