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
package com.imaginea.brightest.test;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.imaginea.brightest.ExecutionContext;

/**
 * Test runner which bridges the gap between old and new junit.
 */
public class BrightestTestRunner extends Runner {
    private static final Log LOG = LogFactory.getLog(BrightestTestRunner.class);
    private BrightestTestSuite suite = null;
    private Description suiteDesc;

    public BrightestTestRunner(Class<?> testClass) {
        if (testClass != BrightestTestSuite.class) {
            throw new IllegalArgumentException("We can only run BrighTest tests using this runner");
        }
    }

    @Override
    public Description getDescription() {
        Description suiteDesc = Description.createSuiteDescription(BrightestTestSuite.class);
        try {
            suite = new BrightestTestSuite(true);
            for (int i = 0; i < suite.testCount(); i++) {
                Test test = suite.testAt(i);
                if (test instanceof TestSuite) {
                    suiteDesc.addChild(Description.createTestDescription(TestSuite.class, ((TestSuite) test).getName()));
                } else {
                    suiteDesc.addChild(Description.createTestDescription(TestCase.class, ((TestCase) test).getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.debug("Created suite " + suiteDesc);
        this.suiteDesc = suiteDesc;
        return suiteDesc;
    }

    @Override
    public void run(RunNotifier notifier) {
        ExecutionContext context = ExecutionContext.getInstance();
        context.start();
        notifier.fireTestStarted(suiteDesc);
        TestResult result = new TestResult();
        result.addListener(createNotifier(notifier));
        if (suite != null) {
            suite.run(result);
        }
        notifier.fireTestFinished(suiteDesc);
        context.stop();
    }

    private TestListener createNotifier(RunNotifier notifier) {
        return new ListenerNotifierAdaptingListener(notifier);
    }

    private Description getDescription(Description suite, String name) {
        for (Description childSuite : suite.getChildren()) {
            if (childSuite.getDisplayName().contains(name)) {
                return childSuite;
            }
        }
        return null;
    }

    private final class ListenerNotifierAdaptingListener implements TestListener {
        private final RunNotifier fNotifier;

        private ListenerNotifierAdaptingListener(RunNotifier notifier) {
            fNotifier = notifier;
        }

        public void endTest(Test test) {
            fNotifier.fireTestFinished(asDescription(test));
        }

        public void startTest(Test test) {
            fNotifier.fireTestStarted(asDescription(test));
        }

        // Implement junit.framework.TestListener
        public void addError(Test test, Throwable t) {
            Failure failure = new Failure(asDescription(test), t);
            fNotifier.fireTestFailure(failure);
        }

        private Description asDescription(Test test) {
            return getDescription(suiteDesc, getName(test));
        }

        private String getName(Test test) {
            if (test instanceof TestCase)
                return ((TestCase) test).getName();
            else
                return test.toString();
        }

        public void addFailure(Test test, AssertionFailedError t) {
            addError(test, t);
        }
    }
}
// extends BlockJUnit4ClassRunner {
// public BrightestTestRunner(Class<?> klass) throws InitializationError {
// super(klass);
// }
//
// protected List<FrameworkMethod> computeTestMethods() {
// return Collections.<FrameworkMethod> emptyList();
// }
// }
