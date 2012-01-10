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
package com.imaginea.brightest.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.imaginea.brightest.Command;
import com.imaginea.brightest.test.CommandBasedTest;
import com.imaginea.brightest.test.CommandBasedTestGroup;
import com.imaginea.brightest.util.Util;

public class XLSFormatHandlerTest {
    @Test
    public void parseCommand() {
        String[] values = new String[] {
                "type(//input[@id='email'], apurba.n@imaginea.com)",
                "type(//input[@id='email']| apurba.n@imaginea.com)",
                "type(//input[@id='email'],apurba.n@imaginea.com)",
                "type( //input[@id='email'] , apurba.n@imaginea.com)", };
        XLSFormatHandler.CommandRow cmdRow = new XLSFormatHandler.CommandRow();
        for (String value : values) {
            Command command = cmdRow.parse(value);
            Assert.assertEquals("type", command.getName());
            Assert.assertEquals("//input[@id='email']", command.getRuntimeArgument());
            Assert.assertEquals("apurba.n@imaginea.com", command.getRuntimeOptionalArgument());
        }
    }

    @Test
    public void parseCommandEmptyString() {
        String[] values = new String[] { "type()", "type(       )" };
        XLSFormatHandler.CommandRow cmdRow = new XLSFormatHandler.CommandRow();
        for (String value : values) {
            Command command = cmdRow.parse(value);
            Assert.assertEquals("type", command.getName());
            Assert.assertNull(command.getRuntimeArgument());
            Assert.assertNull(command.getRuntimeOptionalArgument());
        }
    }

    @Test
    public void understandsXLS() {
        XLSFormatHandler handler = new XLSFormatHandler();
        Assert.assertTrue(handler.understands("abc.xls"));
    }

    @Test
    public void loadSuite() throws Exception {
        File tmpInputFile = createDummyFile();
        Util.copyStream(Util.getResourceAsStream("test/GoogleSearch.xls"), new FileOutputStream(tmpInputFile));
        XLSFormatHandler formatHandler = new XLSFormatHandler();
        CommandBasedTestGroup suite = formatHandler.loadTestSuite(tmpInputFile.getAbsolutePath());
        Assert.assertNotNull(suite);
        Assert.assertEquals(1, suite.countTestCases());
        CommandBasedTest test = suite.testAt(0);
        Assert.assertEquals(2, test.commandCount());
    }

    private File createDummyFile() throws IOException {
        File tmpInputFile = File.createTempFile("tempTest", ".xls");
        tmpInputFile.deleteOnExit();
        return tmpInputFile;
    }
}
