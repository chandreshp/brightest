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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class HierarchicalPropertiesTest {
    private static final String KEY = "id";

    @Test
    public void getPropertyTopmost() throws Exception {
        String value = "1";
        HierarchicalProperties testProperties = new HierarchicalProperties();
        File tempFile1 = createTempProperties("first", KEY);
        testProperties.addProperties(tempFile1.getAbsolutePath());
        testProperties.setProperty(KEY, value);
        Assert.assertEquals(value, testProperties.getProperty(KEY));
    }

    @Test
    public void getPropertyHierarchicalWithTopmost() throws Exception {
        HierarchicalProperties testProperties = new HierarchicalProperties();
        File topFile = createTempProperties("first", KEY);
        File lowestFile = createTempProperties("second", KEY);
        testProperties.addProperties(topFile.getAbsolutePath(), lowestFile.getAbsolutePath());
        Assert.assertEquals(topFile.getAbsolutePath(), testProperties.getProperty(KEY));
    }

    @Test
    public void getProeprtyHierarchicalWithLowest() throws Exception {
        HierarchicalProperties testProperties = new HierarchicalProperties();
        File topFile = createTempProperties("first", KEY);
        File lowestFile = createTempProperties("second", KEY + 1);
        testProperties.addProperties(topFile.getAbsolutePath(), lowestFile.getAbsolutePath());
        Assert.assertEquals(lowestFile.getAbsolutePath(), testProperties.getProperty(KEY + 1));
    }

    @Test
    public void missingPropertiesFile() throws Exception {
        String value = "2";
        HierarchicalProperties testProperties = new HierarchicalProperties();
        testProperties.addProperties("nonexistingfile.properties");
        testProperties.setProperty(KEY, value);
        Assert.assertEquals(value, testProperties.getProperty(KEY));
    }

    @Test
    public void propertyKeys() throws Exception {
        HierarchicalProperties testProperties = new HierarchicalProperties();
        File topFile = createTempProperties("first", KEY);
        File lowestFile = createTempProperties("second", KEY + 1);
        testProperties.addProperties(topFile.getAbsolutePath(), lowestFile.getAbsolutePath());
        Set<String> propertyNames = testProperties.propertyNameSet();
        Assert.assertTrue("Should have contained key from top file", propertyNames.contains(KEY));
        Assert.assertTrue("Should have contained key from Lowest file", propertyNames.contains(KEY + 1));
    }

    private File createTempProperties(String fileName, String key) throws IOException {
        File tempFile1 = File.createTempFile(fileName, ".properties");
        FileWriter writer = new FileWriter(tempFile1);
        writer.write(key + "=" + tempFile1.getAbsolutePath());
        writer.flush();
        writer.close();
        return tempFile1;
    }
}
