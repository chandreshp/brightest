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
package com.imaginea.brightest;

import junit.framework.Assert;
import junit.framework.TestSuite;

import org.junit.Test;

import com.imaginea.brightest.format.FormatHandler;
import com.imaginea.brightest.format.UnknownFormatException;

public class TestManagerTest {
    /**
     * Tests the addFormatter and formatter callbacks
     * 
     * @throws Exception
     */
    @Test
    public void loadSuite() throws Exception {
        TestManager fixture = new TestManager();
        TestSuite dummySuite = new TestSuite();
        String testFile = "abc.1223";
        FormatHandler formatHandler = new DummyFormatHandler(dummySuite, testFile);
        fixture.addFormatHandler(formatHandler);
        TestSuite suite = fixture.loadSuite(testFile);
        Assert.assertEquals(dummySuite, suite);
    }

    @Test(expected = UnknownFormatException.class)
    public void loadSuiteUnknown() throws Exception {
        TestManager fixture = new TestManager();
        String testFile = "abc.1223";
        fixture.loadSuite(testFile);
    }

    /**
     * Static mock for FormatHandler
     */
    private static class DummyFormatHandler extends FormatHandler {
        private final TestSuite dummyTestSuite;
        private final String fileName;

        private DummyFormatHandler(TestSuite dummyTestSuite, String fileName) {
            this.dummyTestSuite = dummyTestSuite;
            this.fileName = fileName;
        }

        @Override
        protected boolean understands(String fileName) {
            Assert.assertEquals(this.fileName, fileName);
            return true;
        }

        @Override
        protected TestSuite loadSuiteInternal(String fileName) {
            Assert.assertEquals(this.fileName, fileName);
            return dummyTestSuite;
        }
    }
}
