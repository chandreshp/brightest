/**
 * See README.txt for license information
 */
package com.pramati.brightest.neo;

import java.util.ArrayList;
import java.util.List;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.format.CSVFormatHandler;
import com.imaginea.brightest.format.FormatHandler;
import com.imaginea.brightest.format.HTMLFormatHandler;
import com.imaginea.brightest.format.XLSFormatHandler;
import com.imaginea.brightest.test.CommandBasedTest;
import com.imaginea.brightest.test.CommandBasedTestGroup;
import com.imaginea.brightest.util.HierarchicalProperties;

/**
 * Adapter to convert tests into a format that the plugin understands
 */
public class TestAdapter {
    // this string will hold a JS code snippet that will evaluate to create a testgroup structure
    private final List<FormatHandler> formatHandlers = new ArrayList<FormatHandler>();
    private String javascriptConstructForTestSuite;
    private String[] additionalTestInfo;
    private volatile boolean processed = false;

    public TestAdapter() {
        formatHandlers.add(new XLSFormatHandler());
        formatHandlers.add(new CSVFormatHandler());
        formatHandlers.add(new HTMLFormatHandler());
    }

    public String processsTest(String filePath) {
        try {
            CommandBasedTestGroup testSuite = null;
            for (FormatHandler formatHandler : formatHandlers) {
                testSuite = formatHandler.loadTestSuite(filePath);
                if (testSuite != null) {
                    break;
                }
            }
            if (testSuite != null) {
                String tcName;
                StringBuffer buffer = new StringBuffer("(function(){var suite = new TestSuite();suite.file=FileUtils.getFile(\"");
                buffer.append(escapeValue(filePath)).append("\");suite.tests=[");
                for (int i = 0; i < testSuite.countTestCases(); i++) {
                    CommandBasedTest testCase = testSuite.testAt(i);
                    tcName = escapeValue(testCase.getName());
                    // The Java classes are not really 'complete' for a smooth iteration
                    buffer.append("(function(){var tc=new TestCase(\"").append(tcName).append("\");").append("var tcWrap= new TestSuite.TestCase(suite,null,\"").append(tcName).append(
                            "\");tcWrap.content=tc;").append("tc.setAdditionalParams(\"").append(escapeValue(testCase.getDescription())).append("\",").append("\"").append(
                            escapeValue(testCase.getExpectedResult())).append("\",").append("\"").append(escapeValue(testCase.getTags())).append("\");").append("tc.commands=[");
                    for (int j = 0; j < testCase.commandCount(); j++) {
                        Command instruction = testCase.commandAt(j);
                        buffer.append("new Command(");
                        buffer.append("\"").append(escapeValue(instruction.getName())).append("\"");
                        String argument = instruction.getRuntimeArgument();
                        if (argument != null) {
                            buffer.append(",\"").append(escapeValue(argument)).append("\"");
                        }
                        String optionalArgument = instruction.getRuntimeOptionalArgument();
                        if (optionalArgument != null) {
                            buffer.append(",\"").append(escapeValue(optionalArgument)).append("\"");
                        }
                        buffer.append("),");
                    }
                    buffer.append("];return tcWrap;}()),");
                }
                buffer.append("];return suite;}())");
                javascriptConstructForTestSuite = buffer.toString();
            } else {
                javascriptConstructForTestSuite = "could not read the test suite from the file ";
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            System.out.flush();
            System.err.flush();
            javascriptConstructForTestSuite = exc + "Could not process file, check java console for details";
        }
        processed = true;
        return javascriptConstructForTestSuite;
    }

    private String escapeValue(String val) {
        return val != null ? val.replaceAll("([\\\\\\\"])", "\\$1") : "";
    }

    public String[] getTestDescription() {
        return additionalTestInfo;
    }

    public boolean isProcessed() {
        if (processed == false) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return processed;
    }

    public void processFile(final String filePath) {
        System.out.println("Starting to process");
        processed = false;
        Thread processor = new Thread(new Runnable() {
            public void run() {
                processsTest(filePath);
            }
        });
        processor.start();
    }

    public void writeFile(String filePath, String[] rawCommands) {
        ExcelWriter writer = new ExcelWriter(filePath);
        writer.writeCommands(rawCommands);
    }

    public void writeResults(String filePath, final String[] rawResults) {
        Writer writer = getWriter(filePath);
        writeResultsWithWriter(rawResults, writer);
    }

    private Writer getWriter(String filePath) {
        Writer writer = null;
        if (filePath.trim().endsWith(".xls")) {
            writer = new ExcelWriter(filePath);
        } else if (filePath.trim().endsWith(".csv")) {
            writer = new CSVWriter(filePath);
        } else if (filePath.trim().endsWith(".html")) {
            writer = new HTMLWriter(filePath);
        }
        return writer;
    }

    private void writeResultsWithWriter(final String[] rawResults, final Writer writer) {
        new Thread(new Runnable() {
            public void run() {
                writer.writeResults(rawResults);
            }
        }).start();
    }

    /** package */
    String[] getCommandTokenAndArgsAsArray(String comp) {
        int argStart = comp.indexOf('(');
        if (argStart == -1) {
            return null;
        }
        String commandToken = comp.substring(0, argStart);
        String firstArg = comp.substring(argStart + 1);
        String secondArg = null;
        if (firstArg.indexOf(',') != -1) {
            String[] splits = firstArg.split(",");
            firstArg = splits[0];
            secondArg = splits[1].substring(0, splits[1].length() - 1);
        } else {
            firstArg = firstArg.substring(0, firstArg.length() - 1);
        }
        if (commandToken != null)
            commandToken = commandToken.trim();
        if (firstArg != null)
            firstArg = firstArg.trim();
        if (secondArg != null)
            secondArg = secondArg.trim();
        if (firstArg.contains("|") && (secondArg == null || secondArg.trim().equals(""))) {
            int firstArgEnd = firstArg.indexOf("|");
            String legacyString = firstArg;
            firstArg = legacyString.substring(0, firstArgEnd);
            if (legacyString.length() > (firstArgEnd + 1)) {
                secondArg = legacyString.substring(firstArgEnd + 1);
            }
        }
        return new String[] { commandToken, firstArg, secondArg };
    }

    public String getJavascriptConstructForTestSuite() {
        return javascriptConstructForTestSuite;
    }

    /**
     * Gets a json string with a properties map for all hierarchical properties
     * 
     * @param files
     * @return
     */
    public String getProperties(String files) {
        String jsonResult = null;
        try {
            if (files != null) {
                String[] splits = files.split(";|:");
                HierarchicalProperties properties = new HierarchicalProperties();
                properties.addProperties(splits);
                // trying to circumvent more dependencies
                StringBuilder jsonBuilder = new StringBuilder();
                jsonBuilder.append('{');
                for (String key : properties.propertyNameSet()) {
                    jsonBuilder.append('"').append(key).append('"').append(':').append('"').append(properties.getProperty(key)).append('"').append(',');
                }
                jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
                jsonBuilder.append('}');
                jsonResult = jsonBuilder.toString();
            } else {
                jsonResult = "{}";
            }
        } catch (Exception exc) {
            jsonResult = "{\"failure\":\"" + exc.getMessage() + "\"}";
        }
        return jsonResult;
    }
}
