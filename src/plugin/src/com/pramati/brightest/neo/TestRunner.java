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
package com.pramati.brightest.neo;

import junit.framework.TestSuite;

import org.json.JSONMap;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.TestManager;
import com.imaginea.brightest.test.CommandBasedTestCase;
import com.imaginea.brightest.util.HierarchicalProperties;

public class TestRunner {
    // this string will hold a JS code snippet that will evaluate to create a testsuite structure
    private String javascriptConstructForTestSuite;
    private String[] additionalTestInfo;
    private volatile boolean processed = false;

    public String processsTest(String filePath) {
        try {
            TestManager mgr = new TestManager();
            TestSuite testSuite = mgr.loadSuite(filePath);
            String tcName;
            StringBuffer buffer = new StringBuffer("(function(){var suite = new TestSuite();suite.file=FileUtils.getFile(\"");
            buffer.append(escapeValue(filePath)).append("\");suite.tests=[");
            for (int i = 0; i < testSuite.testCount(); i++) {
                CommandBasedTestCase testCase = (CommandBasedTestCase) testSuite.testAt(i);
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
        ResultWriter writer = null;
        if (filePath.trim().endsWith(".xls")) {
            writer = new ExcelWriter(filePath);
        } else if (filePath.trim().endsWith(".csv")) {
            writer = new CSVWriter(filePath);
        } else if (filePath.trim().endsWith(".html")) {
            writer = new HTMLWriter(filePath);
        }
        writeResultsWithWriter(rawResults, writer);
    }

    private void writeResultsWithWriter(final String[] rawResults, final ResultWriter writer) {
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
                JSONMap map = new JSONMap();
                for (String key : properties.propertyNameSet()) {
                    map.put(key, properties.getProperty(key));
                }
                jsonResult = map.toString();
            } else {
                jsonResult = "{}";
            }
        } catch (Exception exc) {
            jsonResult = "{\"failure\":\"" + exc.getMessage() + "\"}";
        }
        return jsonResult;
    }
}
