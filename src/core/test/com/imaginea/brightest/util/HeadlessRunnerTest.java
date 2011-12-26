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
package com.imaginea.brightest.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.imaginea.brightest.util.OptionParser.ParsingException;

/**
 * Test for headless mode
 * 
 * @author apurba
 */
public class HeadlessRunnerTest {
    @Test(expected = ParsingException.class)
    public void testWithNoArgs() {
        HeadlessRunner runner = new HeadlessRunner();
        runner.test(new String[] {});
    }

    @Test(expected = ParsingException.class)
    public void testFileNoOptions() throws Exception {
        File tmpInputFile = createDummyFile();
        HeadlessRunner runner = new HeadlessRunner();
        runner.test(new String[] { tmpInputFile.getAbsolutePath() });
    }

    @Test
    public void simpleTest() throws Exception {
        File tmpInputFile = createDummyFile();
        Util.copyStream(Util.getResourceAsStream("test/GoogleSearch.xls"), new FileOutputStream(tmpInputFile));
        HeadlessRunner runner = new HeadlessRunner();
        runner.test(new String[] { "-r", tmpInputFile.getParent(), tmpInputFile.getAbsolutePath() });
    }

    private File createDummyFile() throws IOException {
        File tmpInputFile = File.createTempFile("tempTest", ".xls");
        tmpInputFile.deleteOnExit();
        return tmpInputFile;
    }
}
