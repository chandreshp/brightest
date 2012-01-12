package com.imaginea.brightest.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.imaginea.brightest.test.CommandBasedTest;
import com.imaginea.brightest.test.CommandBasedTestGroup;
import com.imaginea.brightest.util.Util;

public class HTMLFormatHandlerTest {
    @Test
    public void understandsHTML() {
        FormatHandler handler = new HTMLFormatHandler();
        Assert.assertTrue(handler.understands("abc.html"));
        Assert.assertTrue(handler.understands("abc.htm"));
    }

    @Test
    public void loadSuite() throws Exception {
        File tmpInputFile = createDummyFile();
        Util.copyStream(Util.getResourceAsStream("test/GoogleSearch.html"), new FileOutputStream(tmpInputFile));
        HTMLFormatHandler formatHandler = new HTMLFormatHandler();
        CommandBasedTestGroup suite = formatHandler.loadTestSuite(tmpInputFile.getAbsolutePath());
        Assert.assertNotNull(suite);
        Assert.assertEquals(1, suite.countTestCases());
        CommandBasedTest test = suite.testAt(0);
        Assert.assertEquals(2, test.commandCount());
    }

    private File createDummyFile() throws IOException {
        File tmpInputFile = File.createTempFile("tempTest", ".html");
        tmpInputFile.deleteOnExit();
        return tmpInputFile;
    }
}
