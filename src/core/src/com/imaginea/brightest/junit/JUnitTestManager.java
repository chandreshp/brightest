/*
 * Copyright (c) 2011 Imaginea Technologies Private Ltd. 
 * Hyderabad, India
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following condition
 * is met:
 *
 *     + Neither the name of Imaginea, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.imaginea.brightest.junit;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imaginea.brightest.ApplicationPreferences;
import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.format.FormatHandler;
import com.imaginea.brightest.format.UnknownFormatException;
import com.imaginea.brightest.test.CommandBasedTest;
import com.imaginea.brightest.test.CommandBasedTestGroup;

/**
 * Manager for all tests, takes care of saving them, loading them and the other fun stuff.
 */
public class JUnitTestManager {
    private static final Log LOG = LogFactory.getLog(JUnitTestManager.class);

    public JUnitTestManager() {
    }

    public TestSuite loadSuite(String filePath) {
        TestSuite testSuite = new JUnitTestSuiteAdapter();
        CommandBasedTestGroup suite = null;
        for (FormatHandler formatHandler : ExecutionContext.getInstance().getFormatHandlers()) {
            suite = formatHandler.loadTestSuite(filePath);
            if (suite != null) {
                break;
            }
        }
        if (suite == null) {
            throw new UnknownFormatException(filePath);
        } else {
            for (CommandBasedTest test : suite.getTests()) {
                JUnitTestCaseAdapter testCase = new JUnitTestCaseAdapter(test);
                testSuite.addTest(testCase);
            }
        }
        return testSuite;
    }

    public TestResult runTest(ApplicationPreferences preferences) {
        try {
            LOG.debug("Executing " + preferences);
            ExecutionContext context = ExecutionContext.getInstance();
            context.updatePreferences(preferences);
            context.start();
            TestSuite suite = loadSuite(preferences.getTestPath());
            TestResult result = new TestResult();
            addResultListener(result);
            suite.run(result);
            return result;
        } finally {
            ExecutionContext.getInstance().stop();
        }
    }

    private void addResultListener(TestResult result) {
        result.addListener(new TestListener() {
            private boolean succesful = true;

            @Override
            public void startTest(Test test) {
                System.out.println("====================================================");
                System.out.println("Started test " + test);
                succesful = true;
            }

            @Override
            public void endTest(Test test) {
                System.out.println("Completed test " + test + " " + ((succesful) ? "Succesfully" : "Unsuccessfully"));
                System.out.println("====================================================");
            }

            @Override
            public void addFailure(Test test, AssertionFailedError t) {
                System.err.println(t);
                succesful = false;
            }

            @Override
            public void addError(Test test, Throwable t) {
                System.err.println(t);
                succesful = false;
            }
        });
    }
}
