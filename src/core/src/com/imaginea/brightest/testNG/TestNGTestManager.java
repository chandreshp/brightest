package com.imaginea.brightest.testNG;

import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.format.FormatHandler;
import com.imaginea.brightest.format.UnknownFormatException;
import com.imaginea.brightest.test.CommandBasedTest;
import com.thoughtworks.selenium.Selenium;

public class TestNGTestManager {
    private CommandBasedTest test;
    private final ExecutionContext context;
	public TestNGTestManager(ExecutionContext context) {
		this.context=context;
	}

	public void loadTest(String filename) {
        for (FormatHandler formatHandler : this.context.getFormatHandlers()) {
			this.test = formatHandler.loadTestCase(filename);
			if (this.test != null) {
				break;
			}
		}
		if (this.test == null) {
			throw new UnknownFormatException(filename);
		}
	}
	
	public void runTest(Selenium selenium) {
		context.getExecutor().setExceutionContext(context);
        context.setSelenium(selenium);
		test.setCommandExecutor(context.getExecutor());
		test.runTest(selenium);
	}
}
