package com.imaginea.brightest.test;

import java.io.File;

import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;

import com.imaginea.brightest.TestManager;
import com.imaginea.brightest.format.UnknownFormatException;
import com.imaginea.brightest.util.Util;

@RunWith(BrightestTestRunner.class)
public class BrightestTestSuite extends TestSuite {
    private static final Log LOG = LogFactory.getLog(BrightestTestSuite.class);
    private static int suiteCounter = 1;

    public BrightestTestSuite() {
        this(false);
    }

    public BrightestTestSuite(boolean folderFound) {
        init(folderFound);
    }

    private void init(boolean discover) {
        if (discover) {
            String testFolderOrFile = System.getProperty("brighTest.path");
            if (Util.isNotBlank(testFolderOrFile)) {
                discoverAndAddTests(testFolderOrFile);
            }
            setName(testFolderOrFile);
        } else {
            setName(this.getClass().getSimpleName() + "_" + suiteCounter++);
        }
    }

    private void discoverAndAddTests(String testFolderOrFile) {
        TestManager mgr = new TestManager();
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
