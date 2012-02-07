package com.imaginea.brightest.testNG;

import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.format.UnknownFormatException;
import com.imaginea.brightest.test.CommandBasedTest;
import com.imaginea.brightest.test.TestManager;
import com.thoughtworks.selenium.Selenium;

/**
 * Understands TestNG test.
 */
public class TestNGTestManager extends TestManager {
    private CommandBasedTest test;

	public TestNGTestManager(ExecutionContext context) {
        super(context);
	}

	public void loadTest(String filename) {
        this.test = loadIndividualTest(filename);
		if (this.test == null) {
			throw new UnknownFormatException(filename);
		}
	}
	
	public void runTest(Selenium selenium) {
		context.getExecutor().setExceutionContext(context);
        context.setSelenium(selenium);
        test.runTest(selenium, context.getExecutor());
	}
}
