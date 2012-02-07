/**
 * Refere to license.txt for license
 */
package com.imaginea.brightest.test;

import java.util.ArrayList;
import java.util.List;

import com.imaginea.brightest.ExecutionContext;
import com.imaginea.brightest.format.FormatHandler;

/**
 * Test Manager which understands how to read tests
 */
public class TestManager {
    protected final ExecutionContext context;
    private List<FormatHandler> formatHandlers;

    public TestManager(ExecutionContext context) {
        this.context = context;
    }

    public TestManager() {
        this(ExecutionContext.getInstance());
    }

    public CommandBasedTestGroup loadTestGroup(String filePath) {
        CommandBasedTestGroup testGroup = null;
        for (FormatHandler formatHandler : getFormatHandlers()) {
            testGroup = formatHandler.loadTestSuite(filePath);
            if (testGroup != null) {
                break;
            }
        }
        return testGroup;
    }

    public CommandBasedTest loadIndividualTest(String filename) {
        CommandBasedTest test = null;
        for (FormatHandler formatHandler : this.context.getFormatHandlers()) {
            test = formatHandler.loadTestCase(filename);
            if (test != null) {
                break;
            }
        }
        return test;
    }

    protected List<FormatHandler> getFormatHandlers() {
        if (this.formatHandlers == null) {
            // we do not set the format handlers field, as we want all changes from context to be reflected
            return context.getFormatHandlers();
        } else {
            return formatHandlers;
        }
    }

    protected void resetFormatHandlers(FormatHandler formatHandler) {
        this.formatHandlers = new ArrayList<FormatHandler>();
        formatHandlers.add(formatHandler);
    }
}
