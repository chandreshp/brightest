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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author apurba
 */
public class TestAdapterTest {
    private File tempTestFile = null;

    @Before
    public void createResources() throws IOException {
        if (tempTestFile == null) {
            prepareDummyTestFile();
        }
    }

    @After
    public void releaseResources() {
        if (tempTestFile != null) {
            tempTestFile.delete();
            tempTestFile = null;
        }
    }

    @Test
    public void testReadingCommands() throws Exception {
        TestAdapter adapter = new TestAdapter();
        adapter.processFile(tempTestFile.getAbsolutePath());
        while (adapter.isProcessed() == false) {
            // do nothing
        }
        String commands = adapter.getJavascriptConstructForTestSuite();
        System.out.println(commands);
        Assert.assertNotNull(commands);
    }

    @Test
    public void testWritingResults() throws IOException {
        String resultStr = tempTestFile.getAbsolutePath() + "~2~Element link=Pramati Technologies not found**$$** 0~3**$$** 1~3";
        TestAdapter adapter = new TestAdapter();
        File tempResultsFile = File.createTempFile("testResults", ".xls");
        tempResultsFile.deleteOnExit();
        adapter.writeResults(tempResultsFile.getAbsolutePath(), new String[] { resultStr });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Assert.fail("Could not wait for completion");
        }
        // TODO need a better check which can read the contents
        Assert.assertFalse("Should have written some results", 0 == tempResultsFile.length());
    }

    @Test
    public void testGetProperties() throws IOException {
        TestAdapter adapter = new TestAdapter();
        StringBuilder locationBuilder = new StringBuilder();
        locationBuilder.append(prepareDummyProperties("TESTKEY", "123"));
        locationBuilder.append(File.pathSeparator);
        locationBuilder.append(prepareDummyProperties("TESTKEY1", "123"));
        String jsonRepresentation = adapter.getProperties(locationBuilder.toString());
        Assert.assertEquals("{\"TESTKEY1\":\"123\",\"TESTKEY\":\"123\"}", jsonRepresentation);
    }

    // TODO would be better to create the data without depending on test adapter to do it.
    private void prepareDummyTestFile() throws IOException {
        TestAdapter adapter = new TestAdapter();
        tempTestFile = File.createTempFile("Dummy", ".xls");
        String[] commands = new String[] {
                "open(https://fictitious.com/fiction/display_company_profile.jsp)",
                "type(//input[@name='username']|superuser)",
                "type(//div[1]/div/div[2]/div[87]/div/div/div/input|random@dummy.com,  no-reply@dummy.com)" };
        String[] reformattedCommands = new String[commands.length];
        for (int i = 0; i < reformattedCommands.length; i++) {
            String comp = commands[i];
            reformattedCommands[i] = reformatCommand(comp, adapter);
        }
        adapter.writeFile(tempTestFile.getAbsolutePath(), reformattedCommands);
    }

    private String prepareDummyProperties(String key, String value) throws IOException {
        File tempPropsFile = File.createTempFile("test", ".properties");
        tempPropsFile.deleteOnExit();
        Properties tempProperties = new Properties();
        tempProperties.setProperty(key, value);
        tempProperties.store(new FileOutputStream(tempPropsFile), "Temp file");
        return tempPropsFile.getAbsolutePath();
    }

    private String reformatCommand(String comp, TestAdapter adapter) {
        String[] tokenAndArgs = adapter.getCommandTokenAndArgsAsArray(comp);
        return String.format("%s~%s~%s", tokenAndArgs[0], tokenAndArgs[1], ((tokenAndArgs[2] == null) ? "" : tokenAndArgs[2].trim()));
    }
}
